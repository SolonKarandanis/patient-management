package com.pm.authservice.domain.port.in;

public interface VerifyTokenUseCase {
    void verify(String token);
}
