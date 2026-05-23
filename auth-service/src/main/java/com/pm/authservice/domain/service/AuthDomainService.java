package com.pm.authservice.domain.service;

import com.pm.authservice.domain.annotation.DomainService;
import com.pm.authservice.domain.exception.BusinessRuleException;
import com.pm.authservice.domain.exception.UserNotFoundException;
import com.pm.authservice.domain.model.User;
import com.pm.authservice.domain.port.in.AuthenticateUserUseCase;
import com.pm.authservice.domain.port.out.PasswordHasher;
import com.pm.authservice.domain.port.out.UserPort;

@DomainService
public class AuthDomainService implements AuthenticateUserUseCase {

    private final UserPort userPort;
    private final PasswordHasher passwordHasher;

    public AuthDomainService(UserPort userPort, PasswordHasher passwordHasher) {
        this.userPort = userPort;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public User authenticate(String email, String password) {
        User user = userPort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("error.user.not.found"));
        if (!passwordHasher.matches(password, user.getPassword())) {
            throw new BusinessRuleException("error.invalid.credentials");
        }
        return user;
    }
}
