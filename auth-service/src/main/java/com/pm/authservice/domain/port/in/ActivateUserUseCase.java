package com.pm.authservice.domain.port.in;

import com.pm.authservice.domain.model.User;

import java.util.UUID;

public interface ActivateUserUseCase {
    User activate(UUID domainId);
}
