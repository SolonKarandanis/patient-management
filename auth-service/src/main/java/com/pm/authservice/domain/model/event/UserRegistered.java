package com.pm.authservice.domain.model.event;

import java.util.UUID;

public record UserRegistered(
        UUID domainId,
        String email,
        String username,
        String applicationUrl
) implements DomainEvent {
}
