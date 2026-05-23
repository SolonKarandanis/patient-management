package com.pm.authservice.domain.port.in;

import com.pm.authservice.domain.model.User;

public interface RegisterUserUseCase {

    record Command(
            String username,
            String email,
            String password,
            String confirmPassword,
            String firstName,
            String lastName,
            String roleName,
            String applicationUrl
    ) {}

    User register(Command command);
}
