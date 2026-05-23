package com.pm.authservice.domain.model.event;

import java.util.UUID;

public record UserDeleted(UUID domainId, String username) implements DomainEvent {
}
