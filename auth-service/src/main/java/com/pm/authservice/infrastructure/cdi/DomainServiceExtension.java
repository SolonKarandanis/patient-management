package com.pm.authservice.infrastructure.cdi;

import com.pm.authservice.domain.annotation.DomainService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.inject.spi.WithAnnotations;

/**
 * CDI portable extension that registers every {@link DomainService}-annotated class
 * as an {@link ApplicationScoped} CDI bean automatically, with no per-class configuration.
 * Activated via META-INF/services/jakarta.enterprise.inject.spi.Extension.
 */
public class DomainServiceExtension implements Extension {

    <T> void registerDomainServiceBean(
            @Observes @WithAnnotations(DomainService.class) ProcessAnnotatedType<T> pat) {
        pat.configureAnnotatedType()
                .add(ApplicationScoped.Literal.INSTANCE);
    }
}
