package com.pm.authservice.infrastructure.application;

import com.pm.authservice.domain.port.in.RegisterUserUseCase;
import com.pm.authservice.infrastructure.messaging.outbox.OutboxService;
import com.pm.authservice.user.dto.CreateUserDTO;
import com.pm.authservice.user.dto.UserDTO;
import com.pm.authservice.user.repository.UserRepository;
import com.pm.authservice.user.service.UserRegistrationService;
import com.pm.authservice.user.service.UserService;
import com.pm.authservice.util.AppConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegisterUserApplicationService implements UserRegistrationService {

    private final RegisterUserUseCase registerUserUseCase;
    private final UserRepository userRepository;
    private final OutboxService outboxService;
    private final UserService userService;

    public RegisterUserApplicationService(RegisterUserUseCase registerUserUseCase,
                                          UserRepository userRepository,
                                          OutboxService outboxService,
                                          UserService userService) {
        this.registerUserUseCase = registerUserUseCase;
        this.userRepository = userRepository;
        this.outboxService = outboxService;
        this.userService = userService;
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
                    return userService.convertToDTO(entity, true);
                })
                .orElseThrow(() -> new IllegalStateException(
                        "User was saved but could not be found by domainId: " + domainUser.getDomainId()));
    }
}
