package com.pm.authservice.service;


import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.user.model.UserEntity;
import com.pm.authservice.model.VerificationTokenEntity;
import com.pm.authservice.repository.VerificationTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

@Service
@Transactional(readOnly = true)
public class VerificationTokenServiceBean implements VerificationTokenService{

    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationTokenServiceBean(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Transactional
    @Override
    public Boolean validateToken(VerificationTokenEntity verificationToken) throws BusinessException {
        if(verificationToken == null){
            throw new BusinessException("error.invalid.token");
        }
        if(verificationToken.getUser().getIsVerified()){
            throw new BusinessException("error.user.already.verified");
        }

        Calendar calendar = Calendar.getInstance();
        if ((verificationToken.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0){
            verificationTokenRepository.delete(verificationToken);
            throw new BusinessException("error.expired.token");
        }
        return true;
    }

    @Override
    public VerificationTokenEntity findByToken(String theToken) {
        return verificationTokenRepository.findByToken(theToken);
    }

    @Transactional
    @Override
    public void saveUserVerificationToken(UserEntity theUser, String token) {
        var verificationToken = new VerificationTokenEntity(token, theUser);
        verificationTokenRepository.save(verificationToken);
    }
}
