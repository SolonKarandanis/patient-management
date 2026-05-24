package com.pm.authservice.infrastructure.application;

public interface VerifyTokenService {
    void verifyEmail(String token);
}
