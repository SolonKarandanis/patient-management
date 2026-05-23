# ADR 0001 — Domain service discovery via @DomainService annotation

**Status:** Accepted

## Context

auth-service is being refactored to hexagonal architecture. Domain services are plain Java classes with no framework annotations. Two frameworks must be able to wire them: Spring (current) and Quarkus (future).

Two approaches were considered:

1. **Manual wiring** — explicit `@Bean` methods in Spring `@Configuration` and `@Produces` methods in a Quarkus CDI producer. Zero annotations in the domain. Completely explicit.

2. **Custom annotation + auto-discovery** — a `@DomainService` annotation in the domain (pure Java, no framework imports). Spring uses `@ComponentScan` with `FilterType.ANNOTATION`. Quarkus uses a CDI portable extension that promotes `@DomainService` classes to `@ApplicationScoped` beans.

## Decision

Use the custom annotation approach (option 2).

## Rationale

Manual wiring requires touching `DomainConfig.java` (Spring) and `DomainProducer.java` (Quarkus) every time a new domain service is added. As the domain grows this becomes a maintenance tax with no architectural benefit.

With the annotation approach, discovery logic is written once per framework and never touched again. A new domain service is registered in both frameworks by adding `@DomainService` — one annotation, zero config changes.

The `@DomainService` annotation itself has zero framework imports (`@Retention` and `@Target` are standard Java). The domain module does not gain any Spring or CDI compile dependency.

## Consequences

- All domain service classes must carry `@DomainService`.
- Spring wiring lives in `infrastructure/config/DomainConfig.java` (one-time setup).
- Quarkus wiring lives in a CDI portable extension registered under `META-INF/services/javax.enterprise.inject.spi.Extension` (one-time setup).
- If a third framework is added later, it needs its own one-time discovery adapter for `@DomainService` — same cost as the Quarkus extension.
