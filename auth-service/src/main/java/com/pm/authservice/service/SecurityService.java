package com.pm.authservice.service;

import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;

public interface SecurityService {

    boolean isSystemAdmin(UserJpaEntity user);
}
