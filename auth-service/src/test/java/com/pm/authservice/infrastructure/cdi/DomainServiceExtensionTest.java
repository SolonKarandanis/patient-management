package com.pm.authservice.infrastructure.cdi;

import com.pm.authservice.domain.annotation.DomainService;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.Test;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.Bean;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DomainServiceExtensionTest {

    @DomainService
    static class ToyDomainService {
        public String ping() {
            return "pong";
        }
    }

    static class PlainService {
        public String ping() {
            return "pong";
        }
    }

    @Test
    void domainServiceAnnotatedClassIsResolvableAsCdiBean() {
        try (WeldContainer container = new Weld()
                .disableDiscovery()
                .addExtension(new DomainServiceExtension())
                .addBeanClass(ToyDomainService.class)
                .initialize()) {

            ToyDomainService bean = container.select(ToyDomainService.class).get();
            assertNotNull(bean);
            assertEquals("pong", bean.ping());
        }
    }

    @Test
    void plainClassWithoutDomainServiceIsNotGivenApplicationScope() {
        try (WeldContainer container = new Weld()
                .disableDiscovery()
                .addExtension(new DomainServiceExtension())
                .addBeanClass(PlainService.class)
                .initialize()) {

            Set<Bean<?>> beans = container.getBeanManager().getBeans(PlainService.class);
            boolean anyAppScoped = beans.stream()
                    .anyMatch(b -> ApplicationScoped.class.equals(b.getScope()));
            assertFalse(anyAppScoped,
                    "Extension must not add @ApplicationScoped to classes without @DomainService");
        }
    }
}
