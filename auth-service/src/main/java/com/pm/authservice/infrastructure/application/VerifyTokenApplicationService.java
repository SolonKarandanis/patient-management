package com.pm.authservice.infrastructure.application;

import com.pm.authservice.domain.port.in.VerifyTokenUseCase;
import com.pm.authservice.exception.NotFoundException;
import com.pm.authservice.outbox.service.OutboxService;
import com.pm.authservice.user.repository.UserRepository;
import com.pm.authservice.user.service.VerifyTokenService;
import com.pm.authservice.util.AppConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class VerifyTokenApplicationService implements VerifyTokenService {

    private final VerifyTokenUseCase verifyTokenUseCase;
    private final UserRepository userRepository;
    private final OutboxService outboxService;

    public VerifyTokenApplicationService(VerifyTokenUseCase verifyTokenUseCase,
                                         UserRepository userRepository,
                                         OutboxService outboxService) {
        this.verifyTokenUseCase = verifyTokenUseCase;
        this.userRepository = userRepository;
        this.outboxService = outboxService;
    }

    @Override
    public void verifyEmail(String token) {
        UUID userDomainId = verifyTokenUseCase.verify(token);
        userRepository.findByDomainId(userDomainId)
                .ifPresentOrElse(
                        entity -> outboxService.createUserEvent(entity, AppConstants.OUTBOX_USER_VERIFIED),
                        () -> { throw new NotFoundException("error.user.not.found"); }
                );
    }
}
