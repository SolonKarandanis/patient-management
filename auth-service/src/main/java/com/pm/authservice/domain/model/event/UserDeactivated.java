package com.pm.authservice.domain.model.event;

import java.util.UUID;

public record UserDeactivated(UUID domainId) implements DomainEvent {
}
