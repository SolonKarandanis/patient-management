package com.pm.authservice.infrastructure.cdi;

import com.pm.authservice.domain.annotation.DomainService;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DomainServiceExtensionTest {

    @DomainService
    static class ToyDomainService {
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
    void plainClassWithoutDomainServiceIsNotResolvable() {
        class NotADomainService {
            public String ping() {
                return "pong";
            }
        }

        try (WeldContainer container = new Weld()
                .disableDiscovery()
                .addExtension(new DomainServiceExtension())
                .initialize()) {

            boolean resolvable = container.select(NotADomainService.class).isResolvable();
            assertNotNull(container); // container started cleanly
            assertEquals(false, resolvable);
        }
    }
}
