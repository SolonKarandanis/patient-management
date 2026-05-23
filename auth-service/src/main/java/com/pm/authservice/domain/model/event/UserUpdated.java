package com.pm.authservice.domain.model.event;

import java.util.UUID;

public record UserUpdated(UUID domainId) implements DomainEvent {
}
