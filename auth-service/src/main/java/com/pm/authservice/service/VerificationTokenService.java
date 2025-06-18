package com.pm.authservice.service;


import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.model.UserEntity;
import com.pm.authservice.model.VerificationTokenEntity;

public interface VerificationTokenService {

    public Boolean validateToken(VerificationTokenEntity verificationToken) throws BusinessException;

    public VerificationTokenEntity findByToken(String theToken);

    public void saveUserVerificationToken(UserEntity theUser, String token);
}
