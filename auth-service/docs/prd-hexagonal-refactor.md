# PRD: Refactor auth-service to Hexagonal Architecture

## Problem Statement

The auth-service is built with a traditional layered architecture where infrastructure concerns (JPA entities annotated with `@Entity`, Spring Data repositories, Spring Security, Kafka producers) are directly coupled to business logic. Service classes import Spring annotations (`@Service`, `@Transactional`), JPA types, and QueryDSL-generated artefacts. This means:

- Domain logic cannot be unit tested without bootstrapping a full Spring application context
- Business rules are entangled with framework lifecycle and transaction management
- The service is tightly bound to Spring Boot — adopting a second runtime (e.g. Quarkus) for any domain logic would require rewriting services from scratch
- `publicId` is used as an external-facing identity in some places and as an internal DB field in others, with no clear naming convention distinguishing the two

## Solution

Refactor auth-service to hexagonal (ports and adapters) architecture. The domain layer becomes a pure Java module with zero framework dependencies. Spring Boot continues to power all infrastructure concerns (HTTP, JPA, Kafka, security) via adapter classes that implement domain-defined port interfaces. A Quarkus CDI portable extension is provided alongside Spring wiring so the domain can be hosted under either runtime.

The refactor is executed incrementally — the User aggregate is migrated first, followed by auth, VerificationToken, and then infrastructure cleanup — so the service remains in a shippable state at each step.

## User Stories

1. As a developer, I want domain service classes to have zero Spring, JPA, or Quarkus imports, so that I can instantiate and test them with plain `new` without starting a container.
2. As a developer, I want to add a new domain service by annotating a class with `@DomainService`, so that both Spring and Quarkus discover it automatically without touching any configuration file.
3. As a developer, I want each use case to be expressed as a single-method interface in `domain/port/in/`, so that an application service can declare exactly the capabilities it depends on and nothing more.
4. As a developer, I want persistence adapters to implement domain out-ports, so that I can swap the persistence technology without changing any domain code.
5. As a developer, I want all `@Transactional` annotations to live on infrastructure application services, so that domain services never need to know about transaction semantics.
6. As a developer, I want the User aggregate and VerificationToken aggregate to be plain Java objects with no JPA annotations, so that I can construct them in unit tests without a database.
7. As a developer, I want domain events to be plain Java records, so that I can assert on them without mocking any Spring infrastructure.
8. As a developer, I want password hashing to be accessed through a `PasswordHasher` out-port, so that the domain never imports Spring Security types.
9. As a developer, I want i18n label lookups to be accessed through a `TranslationPort` out-port, so that the domain never imports Spring's `MessageSource`.
10. As a developer, I want JWT generation and validation to remain entirely in the infrastructure web adapter, so that the domain has no concept of token format.
11. As a developer, I want role and permission checks that hit the database to be expressed as methods on `UserPort`, backed by native queries, so that the domain can delegate without an in-memory collection scan.
12. As a developer, I want each aggregate to use a `UUID domainId` as its sole identity at the port boundary, so that the integer database PK never leaks into domain logic.
13. As a developer, I want the external REST API to continue returning `publicId` in JSON responses, so that frontend and downstream consumers require no changes during the refactor.
14. As a developer, I want the Liquibase changelog to include a migration that renames the `public_id` column to `domain_id` across all affected tables, so that the database column name matches the domain field name.
15. As a developer, I want the outbox event and the User save to always happen in the same database transaction, so that at-least-once delivery guarantees are preserved.
16. As a developer, I want Spring event listeners that publish to Kafka and write UserEvent audit records to live in `infrastructure/messaging/listener/`, so that they are clearly infrastructure concerns and not domain logic.
17. As a developer, I want `UserNotFoundException` and `BusinessRuleException` to be plain Java exceptions in `domain/exception/`, so that the domain can throw them without depending on Spring or HTTP.
18. As a developer, I want all Spring Security configuration (filters, access handlers, method security expressions) to live under `infrastructure/security/`, so that it has no coupling to domain classes.
19. As a developer, I want `UserUtil.hasRole()` logic to become `User.hasRole(String)` and `User.isSystemAdmin()` on the domain aggregate, so that role-checking reads as domain language.
20. As a developer, I want all manual domain-to-JPA-entity mappers to live in `infrastructure/persistence/mapper/`, so that mapping logic is co-located with the persistence adapter and never appears in domain services.
21. As a developer, I want `AuthorityConstants` (role name strings) to live in `domain/model/`, so that the domain can reference role names without importing infrastructure constants.
22. As a developer, I want the Quarkus CDI portable extension to be written and registered once, so that any future `@DomainService` class is automatically discovered by Quarkus without any additional configuration.
23. As a developer, I want `UserDetailsServiceBean` to call `UserPort` directly, so that Spring Security's user-loading bridge does not go through the domain service layer.
24. As a developer, I want `SearchServiceBean` and `AdministrationServiceBean` to remain in `infrastructure/`, so that Elasticsearch orchestration and batch re-indexing are never treated as domain logic.
25. As a developer, I want the incremental migration to leave the service in a deployable state after each aggregate is migrated, so that the refactor does not require a long-lived divergent branch.

## Implementation Decisions

### Package structure
Two top-level packages under `com.pm.authservice`:
- `domain/` — annotation, exception, model, port/in, port/out, service
- `infrastructure/` — application, config, i18n, messaging, persistence, search, security, administration, web

### @DomainService annotation and discovery
A `@DomainService` annotation lives in `domain/annotation/` with `@Retention(RUNTIME)` and no framework imports. Spring discovers annotated classes via `@ComponentScan` with `FilterType.ANNOTATION` in `infrastructure/config/DomainConfig`. Quarkus discovers them via a CDI portable extension that adds `@ApplicationScoped` at build time. See ADR 0001.

### Domain identity
All aggregates use `UUID domainId` as domain identity. The integer database PK exists only inside JPA entities and repository queries — it never appears in a port method signature. The `publicId` Java field and `public_id` DB column are renamed to `domainId` / `domain_id` internally. External JSON responses preserve the `publicId` key via `@JsonProperty("publicId")` on infrastructure DTOs.

### Aggregates
- **User** — aggregate root. Holds `UUID domainId`, credentials, `AccountStatus`, and `Set<Role>`. Domain methods: `hasRole(String)`, `isSystemAdmin()`.
- **Role** — reference entity (pre-seeded, read-only). Loaded with User. `RolePort` provides read-only lookup.
- **VerificationToken** — independent aggregate. `VerificationTokenPort` provides save and findByToken.

### Driven out-ports

| Port | Methods |
|---|---|
| `UserPort` | save, findByDomainId, findByEmail, hasRole, hasPermission, search, findByIdRange, getMinMaxId |
| `RolePort` | findAll, findByIds, findByName |
| `VerificationTokenPort` | save, findByToken |
| `DomainEventPublisher` | publish(DomainEvent) |
| `PasswordHasher` | hash(String), matches(String, String) |
| `TranslationPort` | getLabel(String key, Locale locale) |

### Transaction management
`@Transactional` lives exclusively on application services in `infrastructure/application/`. Each application service opens a transaction, calls the domain use case, and lets persistence and event adapters participate in the same transaction. This preserves the outbox atomicity guarantee (User row + OutboxEvent row in one commit).

### JWT
JWT generation and validation are infrastructure-only. `JwtService` is called directly by `AuthController` after `AuthenticateUserUseCase` returns a domain `User`. No JWT port exists in the domain.

### Persistence adapters
Each out-port has a corresponding `*PersistenceAdapter` in `infrastructure/persistence/adapter/` that uses a `*JpaRepository` and a manual `*Mapper`. No MapStruct — all mappers are hand-written `@Component` classes. QueryDSL predicate-building happens inside the adapter, never in domain services.

### Domain events
Domain events are plain Java records implementing a `DomainEvent` marker interface. Domain services publish via `DomainEventPublisher`. `SpringDomainEventPublisher` in `infrastructure/messaging/` implements the port and delegates to Spring's `ApplicationEventPublisher`. Existing event listener classes move to `infrastructure/messaging/listener/` and react to domain events to write outbox records and send Kafka messages.

### Exceptions
`UserNotFoundException` and `BusinessRuleException` are plain Java in `domain/exception/`. `AuthException` and `GlobalExceptionHandler` stay in `infrastructure/web/exception/`.

### Liquibase migration
A new changelog entry renames `public_id` → `domain_id` in the `users` and `user_event` tables. All JPQL named queries that reference `publicId` are updated to `domainId`. Native queries that reference `public_id` are updated to `domain_id`.

### i18n
The i18n sub-module is treated as a supporting infrastructure service — no hexagonal restructuring. The domain accesses translations only via `TranslationPort`.

### Migration strategy
Incremental strangler fig:
1. Create `domain/` skeleton (annotation, empty models, ports, DomainConfig)
2. Migrate `User` aggregate end-to-end (domain object, ports, adapters, application services, controllers)
3. Delete old `UserServiceBean` once replaced
4. Repeat for `auth`, `VerificationToken`, `Role`
5. Move infrastructure concerns last (security config, messaging, i18n, search)

## Testing Decisions

A good test asserts on observable external behaviour — what a use case returns or what side effects it causes — not on internal implementation details like which mapper method was called or how many repository calls were made.

### Domain services — pure unit tests
Domain services are plain Java. Tests construct them directly with `new`, passing in-memory stub implementations of the out-ports. No Spring context, no database. Assertions target returned domain objects and events captured by a stub `DomainEventPublisher`. These run with `./gradlew test -PskipUnitTests=false`.

### Persistence adapters — integration tests (Testcontainers)
Each `*PersistenceAdapter` is tested against a real Postgres instance via Testcontainers. Tests verify that saving a domain object and loading it back by `domainId` round-trips correctly, and that native queries (`hasRole`, `hasPermission`, `findByIdRange`) return expected results. These run with `./gradlew integrationTest -PskipIntegrationTests=false`.

### Application services — integration tests (@SpringBootTest slice)
Verify that a use case command results in the correct row in the `users` table AND a matching row in the `outbox_event` table — confirming transactional atomicity is preserved.

### Web controllers — MockMvc tests
Test HTTP contract: correct status codes, correct JSON field names (`publicId` not `domainId` in responses), and correct validation error responses for invalid input.

## Out of Scope

- Quarkus runtime deployment — the CDI portable extension is written and registered, but actually running the service under Quarkus is not in scope.
- Changes to any other service (`patient-service`, `billing-service`, etc.).
- Changes to the external REST API contract — all existing endpoint paths, HTTP methods, and JSON field names remain unchanged.
- Changes to the JWT claim structure — the `publicId` claim key in JWT tokens is preserved.
- Elasticsearch index schema changes — the `public_id` field in Elasticsearch documents is not renamed.
- Frontend changes.
- Adding new features to auth-service during the refactor.

## Further Notes

- The `i18n` sub-module has its own full controller/service/repository stack. It is left structurally as-is and treated as a supporting infrastructure service.
- `GenericServiceBean` is a mixed utility class. Its responsibilities are split: translation → `TranslationPort` adapter, pagination helpers → infrastructure utilities, document conversion → persistence mapper.
- The Hazelcast session cluster configuration is untouched.
- Both `pom.xml` and `build.gradle` exist in auth-service. Gradle is authoritative. Any new dependencies added during this refactor must be reflected in both build files.
- Full architectural decisions and glossary are documented in `auth-service/CONTEXT.md`. ADR 0001 captures the `@DomainService` discovery decision.
