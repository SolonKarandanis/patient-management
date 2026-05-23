package com.pm.authservice.service;


import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import com.pm.authservice.model.VerificationTokenEntity;

public interface VerificationTokenService {

    Boolean validateToken(VerificationTokenEntity verificationToken) throws BusinessException;

    VerificationTokenEntity findByToken(String theToken);

    void saveUserVerificationToken(UserJpaEntity theUser, String token);
}
