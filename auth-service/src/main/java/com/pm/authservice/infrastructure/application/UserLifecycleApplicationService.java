package com.pm.authservice.infrastructure.application;

import com.pm.authservice.domain.port.in.ActivateUserUseCase;
import com.pm.authservice.domain.port.in.ChangePasswordUseCase;
import com.pm.authservice.domain.port.in.DeactivateUserUseCase;
import com.pm.authservice.domain.port.in.DeleteUserUseCase;
import com.pm.authservice.domain.port.in.UpdateUserUseCase;
import com.pm.authservice.exception.NotFoundException;
import com.pm.authservice.infrastructure.messaging.outbox.OutboxService;
import com.pm.authservice.user.dto.ChangePasswordDTO;
import com.pm.authservice.user.dto.UpdateUserDTO;
import com.pm.authservice.user.dto.UserDTO;
import com.pm.authservice.user.repository.UserRepository;
import com.pm.authservice.user.service.UserLifecycleService;
import com.pm.authservice.user.service.UserService;
import com.pm.authservice.util.AppConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UserLifecycleApplicationService implements UserLifecycleService {

    private static final String USER_NOT_FOUND = "error.user.not.found";

    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final ActivateUserUseCase activateUserUseCase;
    private final DeactivateUserUseCase deactivateUserUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final UserRepository userRepository;
    private final OutboxService outboxService;
    private final UserService userService;

    public UserLifecycleApplicationService(UpdateUserUseCase updateUserUseCase,
                                           DeleteUserUseCase deleteUserUseCase,
                                           ActivateUserUseCase activateUserUseCase,
                                           DeactivateUserUseCase deactivateUserUseCase,
                                           ChangePasswordUseCase changePasswordUseCase,
                                           UserRepository userRepository,
                                           OutboxService outboxService,
                                           UserService userService) {
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.activateUserUseCase = activateUserUseCase;
        this.deactivateUserUseCase = deactivateUserUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.userRepository = userRepository;
        this.outboxService = outboxService;
        this.userService = userService;
    }

    @Override
    public UserDTO updateUser(String publicId, UpdateUserDTO dto) {
        UUID domainId = UUID.fromString(publicId);
        var command = new UpdateUserUseCase.Command(
                dto.getUsername(),
                dto.getEmail(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getRole()
        );
        updateUserUseCase.update(domainId, command);
        return userRepository.findByDomainId(domainId)
                .map(entity -> {
                    outboxService.createUserEvent(entity, AppConstants.OUTBOX_USER_UPDATED);
                    return userService.convertToDTO(entity, true);
                })
                .orElseThrow(() -> new IllegalStateException("User not found after update: " + publicId));
    }

    @Override
    public void deleteUser(String publicId) {
        UUID domainId = UUID.fromString(publicId);
        userRepository.findByDomainId(domainId)
                .ifPresentOrElse(
                        entity -> {
                            outboxService.createUserEvent(entity, AppConstants.OUTBOX_USER_DELETED);
                            deleteUserUseCase.delete(domainId);
                        },
                        () -> { throw new NotFoundException(USER_NOT_FOUND); }
                );
    }

    @Override
    public UserDTO changePassword(String publicId, ChangePasswordDTO dto) {
        UUID domainId = UUID.fromString(publicId);
        var command = new ChangePasswordUseCase.Command(dto.getPassword(), dto.getConfirmPassword());
        changePasswordUseCase.changePassword(domainId, command);
        return userRepository.findByDomainId(domainId)
                .map(entity -> {
                    outboxService.createUserEvent(entity, AppConstants.OUTBOX_USER_UPDATED);
                    return userService.convertToDTO(entity, true);
                })
                .orElseThrow(() -> new IllegalStateException("User not found after password change: " + publicId));
    }

    @Override
    public UserDTO activateUser(String publicId) {
        UUID domainId = UUID.fromString(publicId);
        activateUserUseCase.activate(domainId);
        return userRepository.findByDomainId(domainId)
                .map(entity -> {
                    outboxService.createUserEvent(entity, AppConstants.OUTBOX_USER_ACTIVATED);
                    return userService.convertToDTO(entity, true);
                })
                .orElseThrow(() -> new IllegalStateException("User not found after activation: " + publicId));
    }

    @Override
    public UserDTO deactivateUser(String publicId) {
        UUID domainId = UUID.fromString(publicId);
        deactivateUserUseCase.deactivate(domainId);
        return userRepository.findByDomainId(domainId)
                .map(entity -> {
                    outboxService.createUserEvent(entity, AppConstants.OUTBOX_USER_DEACTIVATED);
                    return userService.convertToDTO(entity, true);
                })
                .orElseThrow(() -> new IllegalStateException("User not found after deactivation: " + publicId));
    }
}
