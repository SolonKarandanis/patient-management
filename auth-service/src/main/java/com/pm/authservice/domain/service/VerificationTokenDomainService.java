package com.pm.authservice.domain.service;

import com.pm.authservice.domain.annotation.DomainService;
import com.pm.authservice.domain.exception.BusinessRuleException;
import com.pm.authservice.domain.exception.UserNotFoundException;
import com.pm.authservice.domain.model.User;
import com.pm.authservice.domain.model.VerificationToken;
import com.pm.authservice.domain.port.in.VerifyTokenUseCase;
import com.pm.authservice.domain.port.out.UserPort;
import com.pm.authservice.domain.port.out.VerificationTokenPort;

import java.util.UUID;

@DomainService
public class VerificationTokenDomainService implements VerifyTokenUseCase {

    private final VerificationTokenPort verificationTokenPort;
    private final UserPort userPort;

    public VerificationTokenDomainService(VerificationTokenPort verificationTokenPort, UserPort userPort) {
        this.verificationTokenPort = verificationTokenPort;
        this.userPort = userPort;
    }

    @Override
    public UUID verify(String tokenString) {
        VerificationToken token = verificationTokenPort.findByToken(tokenString)
                .orElseThrow(() -> new BusinessRuleException("error.invalid.token"));

        if (token.isExpired()) {
            throw new BusinessRuleException("error.expired.token");
        }

        User user = userPort.findByDomainId(token.getUserDomainId())
                .orElseThrow(() -> new UserNotFoundException("error.user.not.found"));

        if (Boolean.TRUE.equals(user.getIsVerified())) {
            throw new BusinessRuleException("error.user.already.verified");
        }

        user.setIsVerified(Boolean.TRUE);
        userPort.save(user);

        return user.getDomainId();
    }
}
