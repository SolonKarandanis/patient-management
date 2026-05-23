package com.pm.authservice.user.service;

public interface VerifyTokenService {
    void verifyEmail(String token);
}
