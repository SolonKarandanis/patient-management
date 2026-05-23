package com.pm.authservice.domain.service;

import com.pm.authservice.domain.exception.BusinessRuleException;
import com.pm.authservice.domain.exception.UserNotFoundException;
import com.pm.authservice.domain.model.AccountStatus;
import com.pm.authservice.domain.model.Role;
import com.pm.authservice.domain.model.User;
import com.pm.authservice.domain.model.event.DomainEvent;
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
import com.pm.authservice.domain.port.in.UpdateUserUseCase;
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

    private static final UUID EXISTING_ID = UUID.randomUUID();
    private static final String EXISTING_USERNAME = "existing";
    private static final String EXISTING_EMAIL = "existing@example.com";

    @BeforeEach
    void setUp() {
        publishedEvents = new ArrayList<>();

        userPort = new UserPort() {
            @Override public User save(User u) { return u; }
            @Override public Optional<User> findByDomainId(UUID id) { return Optional.empty(); }
            @Override public Optional<User> findByEmail(String email) { return Optional.empty(); }
            @Override public Optional<User> findByUsername(String username) { return Optional.empty(); }
            @Override public void delete(UUID id) {}
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
            @Override public void delete(UUID id) {}
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
            @Override public void delete(UUID id) {}
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

    // ---- update ----

    @Test
    void update_returnsUpdatedUser() {
        User existing = User.builder()
                .domainId(EXISTING_ID).username(EXISTING_USERNAME).email(EXISTING_EMAIL)
                .firstName("Old").lastName("Name").status(AccountStatus.ACTIVE)
                .isEnabled(true).isVerified(true).build();
        userPort = userPortWith(existing);
        service = new UserDomainService(userPort, rolePort, eventPublisher, passwordHasher);

        var command = new UpdateUserUseCase.Command(
                "newuser", "new@example.com", "New", "Name", "ROLE_PATIENT");
        User updated = service.update(EXISTING_ID, command);

        assertEquals("newuser", updated.getUsername());
        assertEquals("new@example.com", updated.getEmail());
        assertEquals("New", updated.getFirstName());
        assertEquals(1, publishedEvents.size());
        assertInstanceOf(UserUpdated.class, publishedEvents.get(0));
    }

    @Test
    void update_throwsWhenUserNotFound() {
        assertThrows(UserNotFoundException.class,
                () -> service.update(UUID.randomUUID(), new UpdateUserUseCase.Command(
                        "x", "x@x.com", "X", "X", "ROLE_PATIENT")));
    }

    // ---- delete ----

    @Test
    void delete_publishesUserDeletedEvent() {
        User existing = User.builder()
                .domainId(EXISTING_ID).username(EXISTING_USERNAME).email(EXISTING_EMAIL).build();
        userPort = userPortWith(existing);
        service = new UserDomainService(userPort, rolePort, eventPublisher, passwordHasher);

        service.delete(EXISTING_ID);

        assertEquals(1, publishedEvents.size());
        UserDeleted event = (UserDeleted) publishedEvents.get(0);
        assertEquals(EXISTING_ID, event.domainId());
        assertEquals(EXISTING_USERNAME, event.username());
    }

    @Test
    void delete_throwsWhenUserNotFound() {
        assertThrows(UserNotFoundException.class, () -> service.delete(UUID.randomUUID()));
    }

    // ---- activate ----

    @Test
    void activate_setsStatusActiveAndPublishesEvent() {
        User existing = User.builder()
                .domainId(EXISTING_ID).username(EXISTING_USERNAME).email(EXISTING_EMAIL)
                .status(AccountStatus.INACTIVE).isEnabled(false).isVerified(false).build();
        userPort = userPortWith(existing);
        service = new UserDomainService(userPort, rolePort, eventPublisher, passwordHasher);

        User result = service.activate(EXISTING_ID);

        assertEquals(AccountStatus.ACTIVE, result.getStatus());
        assertTrue(result.getIsEnabled());
        assertTrue(result.getIsVerified());
        assertEquals(1, publishedEvents.size());
        assertInstanceOf(UserActivated.class, publishedEvents.get(0));
    }

    @Test
    void activate_throwsWhenAlreadyActive() {
        User existing = User.builder()
                .domainId(EXISTING_ID).username(EXISTING_USERNAME).email(EXISTING_EMAIL)
                .status(AccountStatus.ACTIVE).isEnabled(true).isVerified(true).build();
        userPort = userPortWith(existing);
        service = new UserDomainService(userPort, rolePort, eventPublisher, passwordHasher);

        assertThrows(BusinessRuleException.class, () -> service.activate(EXISTING_ID));
        assertTrue(publishedEvents.isEmpty());
    }

    // ---- deactivate ----

    @Test
    void deactivate_setsStatusInactiveAndPublishesEvent() {
        User existing = User.builder()
                .domainId(EXISTING_ID).username(EXISTING_USERNAME).email(EXISTING_EMAIL)
                .status(AccountStatus.ACTIVE).isEnabled(true).isVerified(true).build();
        userPort = userPortWith(existing);
        service = new UserDomainService(userPort, rolePort, eventPublisher, passwordHasher);

        User result = service.deactivate(EXISTING_ID);

        assertEquals(AccountStatus.INACTIVE, result.getStatus());
        assertFalse(result.getIsEnabled());
        assertEquals(1, publishedEvents.size());
        assertInstanceOf(UserDeactivated.class, publishedEvents.get(0));
    }

    @Test
    void deactivate_throwsWhenAlreadyInactive() {
        User existing = User.builder()
                .domainId(EXISTING_ID).username(EXISTING_USERNAME).email(EXISTING_EMAIL)
                .status(AccountStatus.INACTIVE).isEnabled(false).isVerified(false).build();
        userPort = userPortWith(existing);
        service = new UserDomainService(userPort, rolePort, eventPublisher, passwordHasher);

        assertThrows(BusinessRuleException.class, () -> service.deactivate(EXISTING_ID));
        assertTrue(publishedEvents.isEmpty());
    }

    // ---- changePassword ----

    @Test
    void changePassword_updatesHashAndPublishesEvent() {
        User existing = User.builder()
                .domainId(EXISTING_ID).username(EXISTING_USERNAME).email(EXISTING_EMAIL)
                .password("hashed:old").build();
        userPort = userPortWith(existing);
        service = new UserDomainService(userPort, rolePort, eventPublisher, passwordHasher);

        var command = new ChangePasswordUseCase.Command("newpass", "newpass");
        User result = service.changePassword(EXISTING_ID, command);

        assertEquals("hashed:newpass", result.getPassword());
        assertEquals(1, publishedEvents.size());
        assertInstanceOf(UserPasswordChanged.class, publishedEvents.get(0));
    }

    @Test
    void changePassword_throwsWhenPasswordMismatch() {
        User existing = User.builder()
                .domainId(EXISTING_ID).username(EXISTING_USERNAME).email(EXISTING_EMAIL)
                .password("hashed:old").build();
        userPort = userPortWith(existing);
        service = new UserDomainService(userPort, rolePort, eventPublisher, passwordHasher);

        var command = new ChangePasswordUseCase.Command("newpass", "different");
        assertThrows(BusinessRuleException.class, () -> service.changePassword(EXISTING_ID, command));
        assertTrue(publishedEvents.isEmpty());
    }

    // ---- helpers ----

    private UserPort userPortWith(User stored) {
        return new UserPort() {
            @Override public User save(User u) { return u; }
            @Override public Optional<User> findByDomainId(UUID id) {
                return stored.getDomainId().equals(id) ? Optional.of(stored) : Optional.empty();
            }
            @Override public Optional<User> findByEmail(String email) { return Optional.empty(); }
            @Override public Optional<User> findByUsername(String username) { return Optional.empty(); }
            @Override public void delete(UUID id) {}
            @Override public boolean hasRole(UUID id, String role) { return false; }
            @Override public boolean hasPermission(UUID id, String op) { return false; }
        };
    }
}
