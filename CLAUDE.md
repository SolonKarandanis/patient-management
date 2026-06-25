# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

A `GEMINI.md` at the repo root has an additional overview; read it for a complementary summary if needed.
An `AUTH.md` at the repo root documents the full OAuth2/Keycloak login flow end-to-end (backend filter chain, JWT validation, frontend bootstrap, token refresh, logout). Read it before touching auth code.

## Repository layout

This is a polyrepo-in-a-monorepo: each backend service is an **independent build** (no parent POM). Services live at the root and are built/run from their own directory.

Backend (Java 25, Spring Boot 4.0.x):
- `gateway` — Spring Cloud Gateway (WebFlux), routes `/auth`, `/i18n`, `/api/patients`, `/analytics`, etc. to internal services
- `auth-service` — auth (dual-mode: `auth.mode=jwt` custom JWT or `auth.mode=oauth2` Keycloak), Hazelcast-clustered Spring Session (disabled in oauth2 mode), `i18n` sub-module, outbox pattern. Implemented as **hexagonal architecture** (`domain/` ports + models, `infrastructure/` adapters). **Builds with both Maven AND Gradle** (Gradle is canonical — see below)
- `patient-service`, `medical-records-service`, `billing-service`, `payment-service`, `analytics-service`, `notification-service`, `fts-service`, `stream-processor`, `camel-integration` — each Maven-built Spring Boot service
- `integration-tests` — standalone REST-Assured cross-service tests (separate Maven module, Java 21)

Frontend (Angular 21):
- `frontent/` — **note the misspelling**; this is the actual directory name. Uses NgRx Signals, PrimeNG, TailwindCSS, D3, STOMP-over-SockJS, ngx-permissions, ngx-translate. Cypress for e2e.

Infrastructure & tooling:
- `infrasturcture/` — **also misspelled**; `docker-compose.yml` for Kafka, Zookeeper, Kafka Connect (Debezium), Elasticsearch, Kibana, ClickHouse, ActiveMQ Artemis, Prometheus, Grafana. Postgres is **not** in compose — services expect Postgres at `192.168.1.6:5432` (see `application.properties`). Keycloak is also **not** in compose — run it separately at `localhost:8090` (realm: `patient-management`, client: `angular-frontend`).
- `api-requests/`, `grpc-requests/` — sample REST and gRPC client requests (per-service folders)

## Common commands

### Infrastructure (always start first)
```bash
cd infrasturcture && docker compose up -d
```

### Backend — Maven services (all except auth-service)
Build, run, and test each from the service's own directory:
```bash
cd <service>
./mvnw clean install
./mvnw spring-boot:run
./mvnw test -Punit-tests                          # unit tests
./mvnw failsafe:integration-test -Pintegration-tests  # IT (Testcontainers, needs Docker)
./mvnw verify -Pall-tests                         # both
./mvnw test -Punit-tests -Dtest=ClassName#method  # single test
```
Tests are **skipped by default** — the `unit-tests` / `integration-tests` / `all-tests` profiles flip `skip.unit.tests` / `skip.integration.tests`. Without a profile, `./mvnw test` runs nothing.

### Backend — auth-service (Gradle)
auth-service has both `pom.xml` and `build.gradle` checked in, but Gradle is the working build (it defines the `integrationTest` source set, QueryDSL APT, and protobuf plugin wiring). Prefer:
```bash
cd auth-service
./gradlew build
./gradlew bootRun
./gradlew test -PskipUnitTests=false              # unit tests are gated by gradle property
./gradlew integrationTest -PskipIntegrationTests=false
```
Integration tests live in `src/integration-test/java` (a custom source set), not the standard `src/test`.

### Frontend
```bash
cd frontent
npm install
npm start                # ng serve, http://localhost:4200
npm run build
npm test                 # ng test (Jasmine/Karma)
npx cypress open         # e2e
```

### Cross-service integration tests
```bash
cd integration-tests && ./mvnw test    # requires services already running
```

## Service ports & gateway routes

| Service | Port | gRPC | Context path | Gateway path |
|---|---|---|---|---|
| patient-service | 4000 | 9091 | `/patient-service` | `/api/patients/**` |
| billing-service | 4001 | (gRPC server) | — | (internal) |
| analytics-service | 4002 | | `/analytics-service` | `/analytics/**` |
| gateway | 4004 | | — | — |
| auth-service | 4005 | | `/auth-service` | `/auth/**`, `/i18n/**` |
| fts-service | 4006 | | `/fts` | (internal) |
| camel-integration | 4007 | | `/camel-integration` | — |
| payment-service | 4008 | | — | — |
| medical-records-service | 4009 | | — | — |
| notification-service | 4010 | | `/notification-service` | — |
| stream-processor | 4011 | | — | — |
| Keycloak | 8090 | | `/realms/patient-management` | — (external, not in compose) |

Frontend dev server proxies to gateway at 4004. CORS in gateway is configured for `http://localhost:4200`.

## Architecture you can't see from one file

**Event flow (Outbox + Debezium CDC):** Services write domain events to an `outbox_event` table in the same DB transaction as the business write (`<service>/.../outbox/` packages). Debezium (`infrasturcture/connectors/`) tails the WAL and republishes to Kafka topics (`user-events`, `user-email-events`, `notification-events`, etc.). Consumers live under `broker/` packages. **Do not publish to Kafka directly from services** — go through the outbox to preserve at-least-once + transactional consistency. `stream-processor` does Kafka-Streams aggregations on these topics.

**Sync internal calls (gRPC):** patient ↔ billing ↔ analytics use gRPC. Proto files live in each service's `src/main/proto/`. Generated stubs go to `target/generated-sources` (Maven) or `build/generated/source/proto` (Gradle). The `grpc.server.port` property controls the listening port (e.g. patient-service: 9091).

**Real-time to frontend:** ActiveMQ Artemis with STOMP-over-WebSocket (SockJS fallback). Services publish to JMS; the frontend subscribes via `@stomp/stompjs`. Look for `broker/` and `config/broker/` packages.

**Auth & sessions:** auth-service supports two modes controlled by `auth.mode` in `application.properties`:
- `auth.mode=jwt` — auth-service issues custom JWTs (HMAC-signed); Hazelcast cluster (`hazelcast.cluster.members=127.0.0.1:5701,127.0.0.1:5702`) replicates Spring Session. Session-state code must remain serializable. JWT/AES keys live in `application.properties` (dev-only).
- `auth.mode=oauth2` — Keycloak at `localhost:8090` issues RS256 JWTs; `OAuth2SecurityConfiguration` validates them via JWKS; Hazelcast session management is **disabled** (`hazelcast.session.management.enabled=false`). Roles are extracted from `realm_access.roles` claim. The active user is resolved from the Keycloak `email` claim against the local DB.

`GET /public/config` (no auth) exposes the active mode so the frontend can choose its auth path. See `AUTH.md` for the full flow.

Security configs: `WebSecurityConfiguration` (jwt mode), `OAuth2SecurityConfiguration` (oauth2 mode), `WebSecurityClusterConfiguration` (jwt + Hazelcast cluster). Method-level authorization uses custom SpEL via `CustomMethodSecurityExpressionRoot` (`isSystemAdmin()`, `isUserMe()`, `hasPermission()`).

**i18n:** auth-service has a full DB-backed i18n sub-module (`com.pm.authservice.i18n`) with its own controller/service/repository tree, plus property-file fallbacks (`application_errors_en.properties`, `application_ui_labels_*.properties`). `i18n.resources.DB.enabled=true` toggles DB lookups. The frontend consumes via `ngx-translate` against `/i18n/**` through the gateway.

**Database migrations:** Liquibase per-service under `src/main/resources/db/changelog/master.xml`. Each service owns its own Postgres database (`authservice`, `patientservice`, etc.).

**Query layer:** QueryDSL with APT-generated `Q*` types. Maven services declare `querydsl-apt:jakarta`; the Gradle build wires it via `annotationProcessor`. Generated sources land in `target/generated-sources/annotations` (Maven) or `build/generated/sources/annotationProcessor` (Gradle).

## Things to know before editing

- **Two builds in auth-service:** if you change dependencies, update **both** `pom.xml` and `build.gradle` to keep parity (or confirm with the user which is authoritative for the task — Gradle currently wins on test source-set layout).
- **Postgres is external:** services hardcode `jdbc:postgresql://192.168.1.6:5432/...` in `application.properties`. Override via env or Spring profile rather than committing changes.
- **Test profiles are opt-in:** a green `./mvnw install` does not mean tests ran. Always pass `-Punit-tests` (or the Gradle equivalent) when verifying changes.
- **Don't bypass the outbox** for state changes that other services care about — emitting Kafka events directly defeats the consistency guarantee the whole architecture is built on.
- **Folder names `frontent` and `infrasturcture` are intentional** (or at least entrenched). Don't "fix" them as part of an unrelated change.
- **Frontend state:** uses NgRx Signals (`@ngrx/signals`), not classic NgRx store. Look for `*Store` classes under `core/store/` and feature folders.
- **Auth mode switching:** changing `auth.mode` also requires toggling `hazelcast.session.management.enabled` and (in oauth2 mode) having Keycloak running with the `patient-management` realm and `angular-frontend` client configured. The frontend reads the mode from `/auth/public/config` on startup.
- **auth-service is hexagonal:** domain logic lives under `domain/` (ports, models, annotations), all adapters under `infrastructure/` (web, persistence, security, messaging, etc.). Keep domain classes free of Spring/JPA annotations.
