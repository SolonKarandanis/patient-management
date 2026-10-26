# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

A `GEMINI.md` at the repo root has an additional overview; read it for a complementary summary if needed.
An `AUTH.md` at the repo root documents the full OAuth2/Keycloak login flow end-to-end (backend filter chain, JWT validation, frontend bootstrap, token refresh, logout). Read it before touching auth code.

## Repository layout

This is a polyrepo-in-a-monorepo: each backend service is an **independent build** (no parent POM). Services live at the root and are built/run from their own directory.

Backend (Java 25, Spring Boot 4.0.x):
- `gateway` — Spring Cloud Gateway (WebFlux), routes `/auth`, `/i18n`, `/api/patients`, `/analytics`, etc. to internal services
- `auth-service` — auth (dual-mode: `auth.mode=jwt` custom JWT or `auth.mode=oauth2` Keycloak), Hazelcast-clustered Spring Session (disabled in oauth2 mode), `i18n` sub-module, outbox pattern. Implemented as **hexagonal architecture** (`domain/` ports + models, `infrastructure/` adapters). **Builds with both Maven AND Gradle** (Gradle is canonical — see below)
- `ai-service` — Spring AI 2.0.0, Spring Boot 4 / WebFlux (reactive). Multi-provider LLM support (Anthropic, OpenAI, Gemini) selected via `ai.provider` property. JDBC-backed chat memory (`spring_ai_chat_memory` table) with a sliding window of 50 messages. pgvector RAG store (local Ollama embeddings) plus a separate pgvector-backed tool-search index for progressive tool disclosure, local `@Tool`/MCP tool calling, and a startup `KnowledgeSeeder` that ingests markdown knowledge files — see below. Hexagonal architecture: `domain/` (model + `LlmPort` port), `infrastructure/` (single `SpringAiAdapter` + JDBC memory repo — see below). Uses its own Postgres at `192.168.1.6:**5433**/aiservice` (port 5433, not 5432 — separate PG18 instance for pgvector). Maven-built. **Not exposed through the gateway** — auth-service calls it directly via `AiServiceClient` (RestTemplate).
- `patient-service`, `medical-records-service`, `billing-service`, `payment-service`, `analytics-service`, `notification-service`, `fts-service`, `stream-processor`, `camel-integration` — each Maven-built Spring Boot service
- `integration-tests` — standalone REST-Assured cross-service tests (separate Maven module, Java 21)

Frontend (Angular 21):
- `frontent/` — **note the misspelling**; this is the actual directory name. Uses NgRx Signals, PrimeNG, TailwindCSS, D3, STOMP-over-SockJS, ngx-permissions, ngx-translate. Cypress for e2e. See `frontent/AGENTS.md` for Angular/TypeScript coding conventions (standalone components, signals, Signal Forms, `inject()`, accessibility requirements) — read it before making frontend changes. See also `frontent/docs/architecture-boundaries.md` (Sheriff domain boundaries, layering, data-access-service rules) and `frontent/docs/architecture-state-management.md` (Signal Store granularity, store/service/component access rules) — both include hard "ask the user first" gates (shared-code moves, cross-domain API access) that must be respected, not inferred.

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
| ai-service | 4012 | | `/ai-service` | — (internal; auth-service calls it directly) |
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

**AI / LLM layer (ai-service):** `ChatService` (`service/ChatService.java`) is a thin pass-through — `chat()`/`streamChat()` delegate straight to `LlmPort`; it only touches `ChatMemory` directly for auxiliary reads (`getSessionTurnCount`, `clearSession`, `sessionExists`, `getConversationHistory`). The actual conversation loop lives inside `infrastructure/springai/SpringAiAdapter.java`, the sole `LlmPort` implementation: it wraps one Spring AI `ChatClient` (built by `config/ChatModelRegistrar.java`, which switches on `ai.provider` to construct exactly one `AnthropicChatModel`/`OpenAiChatModel`/`GoogleGenAiChatModel` bean — there are no separate per-provider adapter classes) with `defaultAdvisors(MessageChatMemoryAdvisor, QuestionAnswerAdvisor, ToolSearchToolCallingAdvisor, AvailableToolsLoggingAdvisor, TokenCounterAdvisor)`; the memory advisor transparently loads/saves history keyed by `chat_memory_conversation_id` (bound to `sessionId`), so no manual append step exists anywhere. `MessageWindowChatMemory` (configured in `config/ChatMemoryConfig.java`, `maxMessages(50)`) keeps the last 50 messages per session using the custom `infrastructure/memory/JdbcChatMemoryRepository` against the `spring_ai_chat_memory` table. `ArticleController` is a separate, stateless blog-post generator that builds its own `ChatClient` directly with no advisors (no session memory). The RAG vector store (pgvector, HNSW, cosine, **768-dim**) uses **local Ollama `nomic-embed-text`** for embeddings (`spring.ai.ollama.base-url`) regardless of which chat provider is active — OpenAI is no longer used for embeddings (a prior version of this doc said otherwise). **Prompt caching (Anthropic `cache_control`) is enabled** — `ChatModelRegistrar`'s Anthropic branch sets `AnthropicChatOptions.builder().cacheOptions(AnthropicCacheOptions.builder().strategy(AnthropicCacheStrategy.SYSTEM_AND_TOOLS).build())` (a prior version of this doc said caching was off; it was turned on since).

**Tool calling / MCP (ai-service):** `SpringAiAdapter` additionally pulls in, per chat request, every `ToolCallbackProvider` bean via `ObjectProvider<ToolCallbackProvider>.orderedStream()` — local `@Tool` beans plus any remote MCP connection tools — rather than baking them into the static advisor list. `mcp/AiServiceMcpTools` provides 3 local `@Tool`s (`searchClinicalKnowledge`, `listChatSessions`, `getChatSessionTranscript`) exposed as a `ToolCallbackProvider` via `config/McpToolsConfig`. `spring-ai-starter-mcp-client` and `spring-ai-starter-mcp-server` are both on the classpath, but nothing is configured yet in `application.properties` — no remote MCP servers are actually wired in. To avoid sending every tool definition on every LLM call, `ToolSearchToolCallingAdvisor` (from `spring-ai-tool-search-advisor`) does progressive tool disclosure against a `ToolIndex` (`VectorToolIndex`, `config/ToolSearchConfig.java`) backed by its **own** pgvector table, `tool_search_index` (768-dim, HNSW, cosine) — kept deliberately separate from the clinical-knowledge `vector_store` table so tool-description embeddings don't surface in RAG similarity search.

**Knowledge base seeding (ai-service):** `config/KnowledgeSeeder` (`ApplicationRunner`) ingests every `classpath:knowledge/*.md` file (currently `overview.md`, `dashboard.md`, `users.md`, `i18n.md`) into the vector store on each startup, splitting on `## ` headers into one `Document` per section. Idempotent via a SHA-256 content hash per file stored in the `knowledge_seed_log` table: unchanged file → skipped; changed file → old chunks deleted (`metadata->>'source' = filename`) and re-ingested. Seeding failure is caught and logged, not fatal — the app still starts, RAG is just unavailable.

**Database migrations:** Liquibase per-service under `src/main/resources/db/changelog/master.xml`. Each service owns its own Postgres database (`authservice`, `patientservice`, etc.).

**Query layer:** QueryDSL with APT-generated `Q*` types. Maven services declare `querydsl-apt:jakarta`; the Gradle build wires it via `annotationProcessor`. Generated sources land in `target/generated-sources/annotations` (Maven) or `build/generated/sources/annotationProcessor` (Gradle).

## Things to know before editing

- **Two builds in auth-service:** if you change dependencies, update **both** `pom.xml` and `build.gradle` to keep parity (or confirm with the user which is authoritative for the task — Gradle currently wins on test source-set layout).
- **Postgres is external:** most services hardcode `jdbc:postgresql://192.168.1.6:5432/...`. **Exception: ai-service uses port 5433** (`192.168.1.6:5433/aiservice`) — a separate PG18 instance needed for pgvector. Override via env or Spring profile rather than committing changes.
- **Test profiles are opt-in:** a green `./mvnw install` does not mean tests ran. Always pass `-Punit-tests` (or the Gradle equivalent) when verifying changes.
- **Don't bypass the outbox** for state changes that other services care about — emitting Kafka events directly defeats the consistency guarantee the whole architecture is built on.
- **Folder names `frontent` and `infrasturcture` are intentional** (or at least entrenched). Don't "fix" them as part of an unrelated change.
- **Frontend state:** uses NgRx Signals (`@ngrx/signals`), not classic NgRx store. Look for `*Store` classes under `core/store/` and feature folders.
- **Auth mode switching:** changing `auth.mode` also requires toggling `hazelcast.session.management.enabled` and (in oauth2 mode) having Keycloak running with the `patient-management` realm and `angular-frontend` client configured. The frontend reads the mode from `/auth/public/config` on startup.
- **auth-service is hexagonal:** domain logic lives under `domain/` (ports, models, annotations), all adapters under `infrastructure/` (web, persistence, security, messaging, etc.). Keep domain classes free of Spring/JPA annotations.
- **ai-service is also hexagonal:** `LlmPort` in `domain/port/` is the sole seam between `ChatService` and the LLM backends. Its only implementation is `SpringAiAdapter`, which wraps whichever Spring AI `ChatModel` `ChatModelRegistrar` (an `ImportBeanDefinitionRegistrar`) constructs based on the `ai.provider` property — there is no per-provider adapter class or `@ConditionalOnProperty` switch. Swap provider by changing `ai.provider` in `application.properties` — no code changes needed.
- **ai-service API keys via env:** `ANTHROPIC_API_KEY`, `OPENAI_API_KEY`, `GEMINI_API_KEY` must be set as env vars; they are never committed. Only Anthropic is active by default (`ai.provider=anthropic`). Embeddings use **local Ollama** (`nomic-embed-text`, no API key, just `localhost:11434` running) — OpenAI/Gemini keys are only needed if you switch `ai.provider` to that chat provider, not for embeddings.
- **ai-service RAG schema:** pgvector tables must pre-exist (`spring.ai.vectorstore.pgvector.initialize-schema=false`). Liquibase changelogs under `db/changelog/`: `00000000000001_vector_store.xml` creates the clinical-knowledge `vector_store` table; `00000000000002_chat_memory.xml` creates `spring_ai_chat_memory`; `00000000000003_knowledge_seed_log.xml` creates the seeding idempotency table; `00000000000004_vector_store_768.xml` drops/recreates `vector_store` at 768 dims (destructive — resets `knowledge_seed_log` too, forcing re-ingest; its own SQL comment claims the move was to Google embeddings, which is stale/wrong — current embeddings are Ollama, see above); `00000000000005_tool_search_index.xml` creates the separate `tool_search_index` table used by tool-search. Run migrations before first start.
