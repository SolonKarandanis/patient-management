package com.pm.authservice.domain.model.event;

import java.util.UUID;

public record UserPasswordChanged(UUID domainId) implements DomainEvent {
}
