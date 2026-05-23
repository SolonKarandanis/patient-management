package com.pm.authservice.domain.service;

import com.pm.authservice.domain.annotation.DomainService;
import com.pm.authservice.domain.exception.BusinessRuleException;
import com.pm.authservice.domain.exception.UserNotFoundException;
import com.pm.authservice.domain.model.AccountStatus;
import com.pm.authservice.domain.model.AuthorityConstants;
import com.pm.authservice.domain.model.Role;
import com.pm.authservice.domain.model.User;
import com.pm.authservice.domain.model.UserSearchCriteria;
import com.pm.authservice.domain.model.event.UserActivated;
import com.pm.authservice.domain.model.event.UserDeactivated;
import com.pm.authservice.domain.model.event.UserDeleted;
import com.pm.authservice.domain.model.event.UserPasswordChanged;
import com.pm.authservice.domain.model.event.UserRegistered;
import com.pm.authservice.domain.model.event.UserUpdated;
import com.pm.authservice.domain.port.in.ActivateUserUseCase;
import com.pm.authservice.domain.port.in.ChangePasswordUseCase;
import com.pm.authservice.domain.port.in.DeactivateUserUseCase;
import com.pm.authservice.domain.port.in.DeleteUserUseCase;
import com.pm.authservice.domain.port.in.RegisterUserUseCase;
import com.pm.authservice.domain.port.in.SearchUsersUseCase;
import com.pm.authservice.domain.port.in.UpdateUserUseCase;
import com.pm.authservice.domain.port.out.DomainEventPublisher;
import com.pm.authservice.domain.port.out.PasswordHasher;
import com.pm.authservice.domain.port.out.RolePort;
import com.pm.authservice.domain.port.out.UserPort;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@DomainService
public class UserDomainService implements RegisterUserUseCase, UpdateUserUseCase,
        DeleteUserUseCase, ActivateUserUseCase, DeactivateUserUseCase, ChangePasswordUseCase,
        SearchUsersUseCase {

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
    public User register(RegisterUserUseCase.Command command) {
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

    @Override
    public User update(UUID domainId, UpdateUserUseCase.Command command) {
        User user = userPort.findByDomainId(domainId)
                .orElseThrow(() -> new UserNotFoundException("error.user.not.found"));

        if (!user.getUsername().equals(command.username()) &&
                userPort.findByUsername(command.username()).isPresent()) {
            throw new BusinessRuleException("error.username.exists");
        }
        if (!user.getEmail().equals(command.email()) &&
                userPort.findByEmail(command.email()).isPresent()) {
            throw new BusinessRuleException("error.email.exists");
        }

        Role role = rolePort.findByName(command.roleName())
                .orElseThrow(() -> new BusinessRuleException("error.role.not.found"));

        user.setUsername(command.username());
        user.setEmail(command.email());
        user.setFirstName(command.firstName());
        user.setLastName(command.lastName());
        user.setLastModifiedDate(LocalDate.now());
        user.setRoles(Set.of(role));

        User saved = userPort.save(user);
        eventPublisher.publish(new UserUpdated(saved.getDomainId()));
        return saved;
    }

    @Override
    public void delete(UUID domainId) {
        User user = userPort.findByDomainId(domainId)
                .orElseThrow(() -> new UserNotFoundException("error.user.not.found"));
        String username = user.getUsername();
        userPort.delete(domainId);
        eventPublisher.publish(new UserDeleted(domainId, username));
    }

    @Override
    public User activate(UUID domainId) {
        User user = userPort.findByDomainId(domainId)
                .orElseThrow(() -> new UserNotFoundException("error.user.not.found"));
        user.activate();
        User saved = userPort.save(user);
        eventPublisher.publish(new UserActivated(saved.getDomainId()));
        return saved;
    }

    @Override
    public User deactivate(UUID domainId) {
        User user = userPort.findByDomainId(domainId)
                .orElseThrow(() -> new UserNotFoundException("error.user.not.found"));
        user.deactivate();
        User saved = userPort.save(user);
        eventPublisher.publish(new UserDeactivated(saved.getDomainId()));
        return saved;
    }

    @Override
    public User changePassword(UUID domainId, ChangePasswordUseCase.Command command) {
        validatePassword(command.password(), command.confirmPassword());
        User user = userPort.findByDomainId(domainId)
                .orElseThrow(() -> new UserNotFoundException("error.user.not.found"));
        user.setPassword(passwordHasher.hash(command.password()));
        user.setLastModifiedDate(LocalDate.now());
        User saved = userPort.save(user);
        eventPublisher.publish(new UserPasswordChanged(saved.getDomainId()));
        return saved;
    }

    @Override
    public SearchUsersUseCase.Result search(SearchUsersUseCase.Query query) {
        boolean isAdmin = userPort.hasRole(query.requestingUserDomainId(), AuthorityConstants.ROLE_SYSTEM_ADMIN);
        UserSearchCriteria criteria = new UserSearchCriteria(
                query.email(), query.username(), query.name(), query.status(), query.roleName(),
                query.searchMethod(), query.page(), query.size(), query.sortColumn(), query.sortDirection()
        );
        List<User> users = userPort.search(criteria, isAdmin);
        long totalCount = userPort.count(criteria, isAdmin);
        return new SearchUsersUseCase.Result(users, totalCount);
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
