package com.pm.authservice.outbox.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pm.authservice.dto.UserDocumentDTO;
import com.pm.authservice.user.model.UserEntity;

import java.util.List;

public interface OutboxService {
    void createUserEvent(UserEntity user, String type);
    void indexUsersByCreatingUserEvents(List<UserDocumentDTO> documents) throws JsonProcessingException;
}
