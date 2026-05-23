package com.pm.authservice.domain.port.in;

import java.util.UUID;

public interface VerifyTokenUseCase {
    UUID verify(String token);
}
