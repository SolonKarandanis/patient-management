package com.pm.authservice.service;

import com.pm.authservice.user.model.UserEntity;

public interface SecurityService {

    boolean isSystemAdmin(UserEntity user);
}
