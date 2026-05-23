package com.pm.authservice.domain.model.event;

import java.util.UUID;

public record UserActivated(UUID domainId) implements DomainEvent {
}
