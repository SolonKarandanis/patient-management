# auth-service — Domain Glossary

## Bounded Context

**auth-service** is a single bounded context responsible for authentication, user & role management, and i18n label storage. It is structured as one hexagon with three domain sub-concerns: `auth`, `user`, and `i18n`.

## Package Architecture

Layers-first hexagonal:

```
com.pm.authservice
│
├── domain/
│   ├── annotation/
│   │   └── DomainService.java              ← pure Java, no framework imports
│   ├── exception/
│   │   ├── UserNotFoundException.java
│   │   └── BusinessRuleException.java
│   ├── model/
│   │   ├── User.java                       ← aggregate root, UUID domainId
│   │   ├── Role.java                       ← reference entity
│   │   ├── Operation.java
│   │   ├── VerificationToken.java          ← separate aggregate
│   │   ├── AccountStatus.java              ← domain enum
│   │   ├── AuthorityConstants.java         ← role name constants (domain concepts)
│   │   └── event/
│   │       ├── DomainEvent.java            ← marker interface
│   │       ├── UserRegistered.java
│   │       ├── UserUpdated.java
│   │       └── UserDeleted.java
│   ├── port/
│   │   ├── in/                             ← one interface per use case, single method each
│   │   │   ├── RegisterUserUseCase.java
│   │   │   ├── UpdateUserUseCase.java
│   │   │   ├── DeleteUserUseCase.java
│   │   │   ├── ActivateUserUseCase.java
│   │   │   ├── DeactivateUserUseCase.java
│   │   │   ├── ChangePasswordUseCase.java
│   │   │   ├── SearchUsersUseCase.java
│   │   │   ├── AuthenticateUserUseCase.java
│   │   │   └── VerifyTokenUseCase.java
│   │   └── out/
│   │       ├── UserPort.java               ← save, findByDomainId, findByEmail, hasRole, hasPermission
│   │       ├── RolePort.java               ← read-only: findAll, findByIds, findByName
│   │       ├── VerificationTokenPort.java  ← save, findByToken
│   │       ├── DomainEventPublisher.java
│   │       ├── PasswordHasher.java         ← hash, matches
│   │       └── TranslationPort.java        ← getLabel(key, locale)
│   └── service/
│       ├── UserDomainService.java          ← @DomainService, implements user use cases
│       ├── AuthDomainService.java          ← @DomainService, implements auth use cases
│       └── VerificationTokenDomainService.java
│
└── infrastructure/
    ├── config/
    │   ├── DomainConfig.java               ← @ComponentScan with @DomainService filter
    │   ├── application/                    ← ApplicationConfig, CachingConfig, ServerConfig,
    │   │                                      ServiceConfigProperties
    │   ├── rest/                           ← RestTemplateConfig, HttpClientConfig,
    │   │   └── interceptor/                   RestTemplateResponseErrorHandler, LoggingInterceptor
    │   ├── broker/                         ← KafkaProducerConfig
    │   └── hazelcast/                      ← HazelcastConfig, HazelcastHttpSessionConfig,
    │                                          DummyHttpSession, Initializer, SecurityInitializer
    ├── web/
    │   ├── controller/                     ← UserController, AuthController, RoleController,
    │   │                                      AdminController, CommonEntitiesController,
    │   │                                      PublicApisController, UserRegistrationController
    │   ├── dto/                            ← HTTP request/response DTOs, Paging, SearchResults
    │   ├── validation/                     ← @Authority, AuthorityValidator
    │   ├── export/                         ← UserCsvExporter, AbstractCsvExporter
    │   └── exception/
    │       ├── GlobalExceptionHandler.java ← @RestControllerAdvice
    │       └── AuthException.java
    ├── application/                        ← thin @Service @Transactional orchestrators
    │   ├── RegisterUserApplicationService.java
    │   └── ...                             ← one per use case group
    ├── persistence/
    │   ├── entity/
    │   │   ├── UserJpaEntity.java          ← @Entity, domainId UUID, integer id internal
    │   │   ├── RoleJpaEntity.java
    │   │   ├── VerificationTokenJpaEntity.java
    │   │   ├── UserEventJpaEntity.java     ← audit entity
    │   │   └── converter/
    │   │       └── AccountStatusConverter.java  ← JPA AttributeConverter
    │   ├── repository/
    │   │   ├── UserJpaRepository.java      ← extends JpaRepository + QuerydslPredicateExecutor
    │   │   ├── RoleJpaRepository.java
    │   │   ├── VerificationTokenJpaRepository.java
    │   │   └── projections/
    │   │       └── MinMaxUserId.java
    │   ├── mapper/                         ← manual mappers (@Component), no MapStruct
    │   │   ├── UserMapper.java
    │   │   └── RoleMapper.java
    │   └── adapter/
    │       ├── UserPersistenceAdapter.java         ← implements UserPort
    │       ├── RolePersistenceAdapter.java         ← implements RolePort
    │       └── VerificationTokenPersistenceAdapter.java
    ├── security/
    │   ├── config/                         ← WebSecurityConfiguration, BaseSecurityConfig,
    │   │                                      WebSecurityClusterConfiguration, MethodSecurityConfig
    │   ├── filter/                         ← JwtAuthenticationFilter
    │   ├── handler/                        ← CustomAccessDeniedHandler, RedactedAuthorizationHandler,
    │   │                                      NoAuthenticationRequestMatcher
    │   ├── provider/                       ← CustomAuthProvider
    │   ├── expression/                     ← CustomMethodSecurityExpressionHandler,
    │   │                                      CustomMethodSecurityExpressionRoot, EmailMatchesDomain
    │   ├── serializer/                     ← RedactedEmailUserDTOSerializer
    │   ├── annotation/                     ← @NoAuthentication, @UserEmail, @Email
    │   ├── JwtService.java                 ← JWT generation and validation (infrastructure only)
    │   ├── UserDetailsServiceBean.java     ← Spring Security bridge, calls UserPort directly
    │   └── BCryptPasswordHasher.java       ← implements PasswordHasher
    ├── messaging/
    │   ├── broker/                         ← KafkaAnalyticsProducer, KafkaNotificationsProducer
    │   ├── outbox/
    │   │   ├── OutboxEvent.java
    │   │   └── OutboxService.java
    │   ├── listener/                       ← @EventListener classes reacting to domain events
    │   │   ├── BaseEventListener.java
    │   │   ├── UserRegistrationEventListener.java
    │   │   ├── UserDeletionEventListener.java
    │   │   └── ...
    │   └── SpringDomainEventPublisher.java ← implements DomainEventPublisher
    ├── search/
    │   ├── UserFullTextSearchService.java
    │   ├── UserFullTextSearchServiceBean.java
    │   ├── FtsUtil.java
    │   ├── SearchServiceBean.java
    │   └── dto/                            ← DocumentSearchRequest, SearchCriterion, PagingFts, etc.
    ├── administration/
    │   └── AdministrationServiceBean.java  ← batch re-indexing orchestration
    ├── i18n/                               ← unchanged internal structure
    │   ├── config/                         ← ChangeableLocaleResolver, MessageSource impls,
    │   │   └── annotation/                    TranslationAdvice, @Translate
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── model/
    │   └── event/                          ← TranslationsUpdatedEvent, TranslationsUpdatedEventListener
    └── util/
        ├── AppConstants.java
        ├── SecurityConstants.java
        ├── CryptoUtil.java
        ├── HttpUtil.java
        ├── MiscUtil.java
        ├── StringUtils.java
        └── CollectionUtil.java
```

## Invariants

- **The domain layer has zero framework dependencies.** No Spring, no JPA, no Quarkus. Domain services are plain Java classes discovered via `@DomainService`. This ensures the domain can be reimplemented under Quarkus or any other container without touching domain code.
- **`@Transactional` never appears in the domain.** Transaction boundaries are owned by application services in `infrastructure/application/`.
- **The integer database PK never crosses the port boundary.** All port methods use `UUID domainId` as identity.
- **JWT is a web adapter concern.** `JwtService` is called directly by `AuthController` after `AuthenticateUserUseCase` returns. The domain does not know JWT exists.

## Aggregates

**User** — aggregate root. Holds identity, credentials, `AccountStatus`, and `Set<Role>`. Exposes `hasRole(String)` and `isSystemAdmin()` as domain methods. Role and permission checks against the database go through `UserPort.hasRole()` and `UserPort.hasPermission()` (native queries, keyed by `domainId`).

**Role** — reference entity, not an aggregate root. Pre-seeded, read-only through the application. Always loaded with `User`. Accessed via `RolePort` (read-only out-port) when assigning roles.

**VerificationToken** — separate aggregate with its own out-port (`VerificationTokenPort`). Queried by token string independently of the User.

## Domain Events

Domain events are plain Java records (`UserRegistered`, `UserDeleted`, `UserUpdated`, etc.). Domain services publish via a `DomainEventPublisher` out-port. `SpringDomainEventPublisher` in infrastructure implements this port and delegates to Spring's `ApplicationEventPublisher`. `@EventListener` handlers in `infrastructure/messaging/listener/` react and write to the outbox.

## Ports

### Driving ports (in-ports) — `domain/port/in/`
One interface per use case, single method each. Domain service classes implement multiple use case interfaces. Application services in infrastructure inject only the use cases they need.

### Driven ports (out-ports) — `domain/port/out/`

| Port | Responsibility |
|---|---|
| `UserPort` | User aggregate persistence + `hasRole`/`hasPermission` native queries |
| `RolePort` | Role reference data lookup (read-only) |
| `VerificationTokenPort` | VerificationToken persistence |
| `DomainEventPublisher` | Publish domain events |
| `PasswordHasher` | Hash and verify passwords |
| `TranslationPort` | Look up i18n label by key and locale |

### Infrastructure-only services (no domain port)
- **JWT** — `JwtService` called directly by `AuthController` after authenticate use case returns
- **i18n** — supporting infrastructure service; domain accesses labels only via `TranslationPort`
- **Elasticsearch/FTS** — indexing triggered via outbox or admin actions, entirely in `infrastructure/search/`
- **`AdministrationService`** — batch re-indexing orchestration, `infrastructure/administration/`
- **`SearchServiceBean`** — coordinates DB and Elasticsearch search backends, `infrastructure/search/`
- **`CommonEntitiesService`** — maps Spring config properties to web DTOs, `infrastructure/web/`

## Domain service wiring

Domain services carry `@DomainService` (pure Java annotation, no framework imports). Spring discovers them via `@ComponentScan` with `FilterType.ANNOTATION` in `DomainConfig`. Quarkus discovers them via a CDI portable extension. See [ADR 0001](docs/adr/0001-domain-service-discovery.md).

## Exception boundaries

Domain exceptions (`UserNotFoundException`, `BusinessRuleException`) are plain Java, thrown when a domain invariant is violated — `domain/exception/`. Infrastructure exceptions (`AuthException` and Spring Security integration) live in `infrastructure/web/exception/`. `GlobalExceptionHandler` catches both and maps to HTTP responses.

## Mapper convention

Domain object ↔ JPA entity mapping is done with manual mapper classes in `infrastructure/persistence/mapper/`. No MapStruct. Mappers are `@Component` beans visible only to persistence adapters.

## Identity convention

All domain aggregates use `UUID domainId` as domain identity. The integer database PK (`id`) is an infrastructure detail — it never appears in a port method signature or a domain object.

`domainId` replaces `publicId` as the Java field name in all domain objects and JPA entities. The DB column is renamed `domain_id` via Liquibase migration. External API surfaces (`publicId` JSON fields, JWT `"publicId"` claim, Elasticsearch `public_id` field) are unchanged — infrastructure DTOs use `@JsonProperty("publicId")` to preserve the external contract.

## Glossary

| Term | Definition |
|---|---|
| **User** | An authenticated principal with identity, credentials, roles, and account status. Primary aggregate root. |
| **Role** | A named authority (e.g. `ROLE_ADMIN`) assigned to a User. Pre-seeded reference data; not mutated through the application. |
| **Operation** | A fine-grained permission key associated with a Role. Used to resolve a User's effective permissions. |
| **VerificationToken** | A time-limited token issued at registration and consumed when the User confirms their email. |
| **AccountStatus** | Lifecycle state of a User account (active, inactive, pending, etc.). Domain enum. |
| **DomainEvent** | A plain Java record representing something that happened in the domain (e.g. `UserRegistered`). Published via `DomainEventPublisher` out-port. |
| **domainId** | The `UUID` identity of an aggregate root. The only identity that crosses the port boundary. Previously named `publicId` in the codebase. |
