package com.pm.authservice.domain.port.in;

import com.pm.authservice.domain.model.User;

import java.util.UUID;

public interface ChangePasswordUseCase {

    record Command(String password, String confirmPassword) {}

    User changePassword(UUID domainId, Command command);
}
