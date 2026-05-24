package com.pm.authservice.infrastructure.application;

import com.pm.authservice.domain.port.in.RegisterUserUseCase;
import com.pm.authservice.infrastructure.messaging.outbox.OutboxService;
import com.pm.authservice.infrastructure.persistence.mapper.UserMapper;
import com.pm.authservice.infrastructure.persistence.repository.UserJpaRepository;
import com.pm.authservice.infrastructure.util.AppConstants;
import com.pm.authservice.infrastructure.web.dto.CreateUserDTO;
import com.pm.authservice.infrastructure.web.dto.UserDTO;
import com.pm.authservice.user.service.UserRegistrationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegisterUserApplicationService implements UserRegistrationService {

    private final RegisterUserUseCase registerUserUseCase;
    private final UserJpaRepository userRepository;
    private final OutboxService outboxService;
    private final UserMapper userMapper;

    public RegisterUserApplicationService(RegisterUserUseCase registerUserUseCase,
                                          UserJpaRepository userRepository,
                                          OutboxService outboxService,
                                          UserMapper userMapper) {
        this.registerUserUseCase = registerUserUseCase;
        this.userRepository = userRepository;
        this.outboxService = outboxService;
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO register(CreateUserDTO dto, String applicationUrl) {
        var command = new RegisterUserUseCase.Command(
                dto.getUsername(),
                dto.getEmail(),
                dto.getPassword(),
                dto.getConfirmPassword(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getRole(),
                applicationUrl
        );

        var domainUser = registerUserUseCase.register(command);

        return userRepository.findByDomainId(domainUser.getDomainId())
                .map(entity -> {
                    outboxService.createUserEvent(entity, AppConstants.OUTBOX_USER_CREATED);
                    return userMapper.toDTO(userMapper.toDomain(entity));
                })
                .orElseThrow(() -> new IllegalStateException(
                        "User was saved but could not be found by domainId: " + domainUser.getDomainId()));
    }
}
