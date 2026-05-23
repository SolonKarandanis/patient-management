package com.pm.authservice.domain.service;

import com.pm.authservice.domain.exception.BusinessRuleException;
import com.pm.authservice.domain.model.AccountStatus;
import com.pm.authservice.domain.model.Role;
import com.pm.authservice.domain.model.User;
import com.pm.authservice.domain.model.event.DomainEvent;
import com.pm.authservice.domain.model.event.UserRegistered;
import com.pm.authservice.domain.port.in.RegisterUserUseCase;
import com.pm.authservice.domain.port.out.DomainEventPublisher;
import com.pm.authservice.domain.port.out.PasswordHasher;
import com.pm.authservice.domain.port.out.RolePort;
import com.pm.authservice.domain.port.out.UserPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserDomainServiceTest {

    private UserPort userPort;
    private RolePort rolePort;
    private List<DomainEvent> publishedEvents;
    private DomainEventPublisher eventPublisher;
    private PasswordHasher passwordHasher;
    private UserDomainService service;

    @BeforeEach
    void setUp() {
        publishedEvents = new ArrayList<>();

        userPort = new UserPort() {
            @Override public User save(User u) { return u; }
            @Override public Optional<User> findByDomainId(UUID id) { return Optional.empty(); }
            @Override public Optional<User> findByEmail(String email) { return Optional.empty(); }
            @Override public Optional<User> findByUsername(String username) { return Optional.empty(); }
            @Override public boolean hasRole(UUID id, String role) { return false; }
            @Override public boolean hasPermission(UUID id, String op) { return false; }
        };

        rolePort = new RolePort() {
            @Override public List<Role> findAll() { return List.of(); }
            @Override public List<Role> findByIds(List<Integer> ids) { return List.of(); }
            @Override public Optional<Role> findByName(String name) {
                return Optional.of(new Role(1, name));
            }
        };

        eventPublisher = event -> publishedEvents.add(event);
        passwordHasher = new PasswordHasher() {
            @Override public String hash(String raw) { return "hashed:" + raw; }
            @Override public boolean matches(String raw, String encoded) { return encoded.equals("hashed:" + raw); }
        };

        service = new UserDomainService(userPort, rolePort, eventPublisher, passwordHasher);
    }

    @Test
    void register_returnsUserWithCorrectFields() {
        var command = new RegisterUserUseCase.Command(
                "john", "john@example.com", "secret", "secret",
                "John", "Doe", "ROLE_PATIENT", "http://localhost"
        );

        User user = service.register(command);

        assertNotNull(user.getDomainId());
        assertEquals("john", user.getUsername());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("hashed:secret", user.getPassword());
        assertEquals(AccountStatus.INACTIVE, user.getStatus());
        assertFalse(user.getIsEnabled());
        assertFalse(user.getIsVerified());
        assertEquals(1, user.getRoles().size());
    }

    @Test
    void register_publishesUserRegisteredEvent() {
        var command = new RegisterUserUseCase.Command(
                "jane", "jane@example.com", "pass1", "pass1",
                "Jane", "Smith", "ROLE_DOCTOR", "http://localhost"
        );

        User user = service.register(command);

        assertEquals(1, publishedEvents.size());
        UserRegistered event = (UserRegistered) publishedEvents.get(0);
        assertEquals(user.getDomainId(), event.domainId());
        assertEquals("jane@example.com", event.email());
        assertEquals("jane", event.username());
        assertEquals("http://localhost", event.applicationUrl());
    }

    @Test
    void register_throwsWhenPasswordMismatch() {
        var command = new RegisterUserUseCase.Command(
                "bob", "bob@example.com", "pass1", "pass2",
                "Bob", "Brown", "ROLE_PATIENT", "http://localhost"
        );

        assertThrows(BusinessRuleException.class, () -> service.register(command));
        assertTrue(publishedEvents.isEmpty());
    }

    @Test
    void register_throwsWhenUsernameAlreadyExists() {
        userPort = new UserPort() {
            @Override public User save(User u) { return u; }
            @Override public Optional<User> findByDomainId(UUID id) { return Optional.empty(); }
            @Override public Optional<User> findByEmail(String email) { return Optional.empty(); }
            @Override public Optional<User> findByUsername(String username) {
                return Optional.of(User.builder().username(username).build());
            }
            @Override public boolean hasRole(UUID id, String role) { return false; }
            @Override public boolean hasPermission(UUID id, String op) { return false; }
        };
        service = new UserDomainService(userPort, rolePort, eventPublisher, passwordHasher);

        var command = new RegisterUserUseCase.Command(
                "taken", "new@example.com", "pass", "pass",
                "First", "Last", "ROLE_PATIENT", "http://localhost"
        );

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.register(command));
        assertEquals("error.username.exists", ex.getMessage());
    }

    @Test
    void register_throwsWhenEmailAlreadyExists() {
        userPort = new UserPort() {
            @Override public User save(User u) { return u; }
            @Override public Optional<User> findByDomainId(UUID id) { return Optional.empty(); }
            @Override public Optional<User> findByEmail(String email) {
                return Optional.of(User.builder().email(email).build());
            }
            @Override public Optional<User> findByUsername(String username) { return Optional.empty(); }
            @Override public boolean hasRole(UUID id, String role) { return false; }
            @Override public boolean hasPermission(UUID id, String op) { return false; }
        };
        service = new UserDomainService(userPort, rolePort, eventPublisher, passwordHasher);

        var command = new RegisterUserUseCase.Command(
                "newuser", "taken@example.com", "pass", "pass",
                "First", "Last", "ROLE_PATIENT", "http://localhost"
        );

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.register(command));
        assertEquals("error.email.exists", ex.getMessage());
    }
}
