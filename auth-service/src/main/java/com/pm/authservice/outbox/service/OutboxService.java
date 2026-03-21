package com.pm.authservice.outbox.service;

import com.pm.authservice.user.model.UserEntity;

public interface OutboxService {
    void createUserEvent(UserEntity user, String type);
}
