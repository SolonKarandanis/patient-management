package com.pm.authservice.service;


import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.model.User;
import com.pm.authservice.model.VerificationToken;

public interface VerificationTokenService {

    public Boolean validateToken(VerificationToken verificationToken) throws BusinessException;

    public VerificationToken findByToken(String theToken);

    public void saveUserVerificationToken(User theUser, String token);
}
