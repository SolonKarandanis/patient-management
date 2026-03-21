package com.pm.authservice.outbox.service;

import com.pm.authservice.user.model.UserEntity;

public interface OutboxService {
    void createEvent(UserEntity user, String type);
}
