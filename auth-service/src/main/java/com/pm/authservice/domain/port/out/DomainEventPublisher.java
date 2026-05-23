package com.pm.authservice.domain.port.out;

import com.pm.authservice.domain.model.event.DomainEvent;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
