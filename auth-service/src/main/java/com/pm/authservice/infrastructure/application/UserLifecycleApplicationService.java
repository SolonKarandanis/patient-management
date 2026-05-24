package com.pm.authservice.infrastructure.application;

import com.pm.authservice.domain.port.in.ActivateUserUseCase;
import com.pm.authservice.domain.port.in.ChangePasswordUseCase;
import com.pm.authservice.domain.port.in.DeactivateUserUseCase;
import com.pm.authservice.domain.port.in.DeleteUserUseCase;
import com.pm.authservice.domain.port.in.UpdateUserUseCase;
import com.pm.authservice.infrastructure.messaging.outbox.OutboxService;
import com.pm.authservice.infrastructure.persistence.mapper.UserMapper;
import com.pm.authservice.infrastructure.persistence.repository.UserJpaRepository;
import com.pm.authservice.infrastructure.util.AppConstants;
import com.pm.authservice.infrastructure.web.dto.ChangePasswordDTO;
import com.pm.authservice.infrastructure.web.dto.UpdateUserDTO;
import com.pm.authservice.infrastructure.web.dto.UserDTO;
import com.pm.authservice.infrastructure.web.exception.NotFoundException;
import com.pm.authservice.infrastructure.application.UserLifecycleService;
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
    private final UserJpaRepository userRepository;
    private final OutboxService outboxService;
    private final UserMapper userMapper;

    public UserLifecycleApplicationService(UpdateUserUseCase updateUserUseCase,
                                           DeleteUserUseCase deleteUserUseCase,
                                           ActivateUserUseCase activateUserUseCase,
                                           DeactivateUserUseCase deactivateUserUseCase,
                                           ChangePasswordUseCase changePasswordUseCase,
                                           UserJpaRepository userRepository,
                                           OutboxService outboxService,
                                           UserMapper userMapper) {
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.activateUserUseCase = activateUserUseCase;
        this.deactivateUserUseCase = deactivateUserUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.userRepository = userRepository;
        this.outboxService = outboxService;
        this.userMapper = userMapper;
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
                    return userMapper.toDTO(userMapper.toDomain(entity));
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
                    return userMapper.toDTO(userMapper.toDomain(entity));
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
                    return userMapper.toDTO(userMapper.toDomain(entity));
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
                    return userMapper.toDTO(userMapper.toDomain(entity));
                })
                .orElseThrow(() -> new IllegalStateException("User not found after deactivation: " + publicId));
    }
}
