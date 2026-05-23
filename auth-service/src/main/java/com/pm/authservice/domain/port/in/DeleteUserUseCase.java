package com.pm.authservice.domain.port.in;

import java.util.UUID;

public interface DeleteUserUseCase {
    void delete(UUID domainId);
}
