package com.pm.authservice.domain.port.in;

import com.pm.authservice.domain.model.User;

import java.util.UUID;

public interface UpdateUserUseCase {

    record Command(
            String username,
            String email,
            String firstName,
            String lastName,
            String roleName
    ) {}

    User update(UUID domainId, Command command);
}
