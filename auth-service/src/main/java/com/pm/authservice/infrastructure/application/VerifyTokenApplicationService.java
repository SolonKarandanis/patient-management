package com.pm.authservice.infrastructure.application;

import com.pm.authservice.domain.port.in.VerifyTokenUseCase;
import com.pm.authservice.infrastructure.web.exception.NotFoundException;
import com.pm.authservice.infrastructure.messaging.outbox.OutboxService;
import com.pm.authservice.infrastructure.persistence.repository.UserJpaRepository;
import com.pm.authservice.user.service.VerifyTokenService;
import com.pm.authservice.infrastructure.util.AppConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class VerifyTokenApplicationService implements VerifyTokenService {

    private final VerifyTokenUseCase verifyTokenUseCase;
    private final UserJpaRepository userRepository;
    private final OutboxService outboxService;

    public VerifyTokenApplicationService(VerifyTokenUseCase verifyTokenUseCase,
                                         UserJpaRepository userRepository,
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
