package com.pm.authservice.domain.port.out;

import com.pm.authservice.domain.model.VerificationToken;

import java.util.Optional;

public interface VerificationTokenPort {
    VerificationToken save(VerificationToken token);
    Optional<VerificationToken> findByToken(String token);
}
