package com.pm.authservice.domain.service;

import com.pm.authservice.domain.annotation.DomainService;
import com.pm.authservice.domain.exception.BusinessRuleException;
import com.pm.authservice.domain.model.AccountStatus;
import com.pm.authservice.domain.model.Role;
import com.pm.authservice.domain.model.User;
import com.pm.authservice.domain.model.event.UserRegistered;
import com.pm.authservice.domain.port.in.RegisterUserUseCase;
import com.pm.authservice.domain.port.out.DomainEventPublisher;
import com.pm.authservice.domain.port.out.PasswordHasher;
import com.pm.authservice.domain.port.out.RolePort;
import com.pm.authservice.domain.port.out.UserPort;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@DomainService
public class UserDomainService implements RegisterUserUseCase {

    private final UserPort userPort;
    private final RolePort rolePort;
    private final DomainEventPublisher eventPublisher;
    private final PasswordHasher passwordHasher;

    public UserDomainService(UserPort userPort,
                             RolePort rolePort,
                             DomainEventPublisher eventPublisher,
                             PasswordHasher passwordHasher) {
        this.userPort = userPort;
        this.rolePort = rolePort;
        this.eventPublisher = eventPublisher;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public User register(Command command) {
        validatePassword(command.password(), command.confirmPassword());

        if (userPort.findByUsername(command.username()).isPresent()) {
            throw new BusinessRuleException("error.username.exists");
        }
        if (userPort.findByEmail(command.email()).isPresent()) {
            throw new BusinessRuleException("error.email.exists");
        }

        Role role = rolePort.findByName(command.roleName())
                .orElseThrow(() -> new BusinessRuleException("error.role.not.found"));

        LocalDate today = LocalDate.now();
        User user = User.builder()
                .domainId(UUID.randomUUID())
                .username(command.username())
                .email(command.email())
                .password(passwordHasher.hash(command.password()))
                .firstName(command.firstName())
                .lastName(command.lastName())
                .status(AccountStatus.INACTIVE)
                .isEnabled(Boolean.FALSE)
                .isVerified(Boolean.FALSE)
                .createdDate(today)
                .lastModifiedDate(today)
                .roles(Set.of(role))
                .build();

        User saved = userPort.save(user);

        eventPublisher.publish(new UserRegistered(
                saved.getDomainId(),
                saved.getEmail(),
                saved.getUsername(),
                command.applicationUrl()
        ));

        return saved;
    }

    private void validatePassword(String password, String confirmPassword) {
        if (password == null || password.isBlank()) {
            throw new BusinessRuleException("error.password.required");
        }
        if (confirmPassword == null || confirmPassword.isBlank()) {
            throw new BusinessRuleException("error.password.confirm.required");
        }
        if (!password.equals(confirmPassword)) {
            throw new BusinessRuleException("error.password.confirm");
        }
    }
}
