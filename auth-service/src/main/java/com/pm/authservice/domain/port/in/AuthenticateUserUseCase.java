package com.pm.authservice.domain.port.in;

import com.pm.authservice.domain.model.User;

public interface AuthenticateUserUseCase {
    User authenticate(String email, String password);
}
