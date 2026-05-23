package com.pm.authservice.infrastructure.messaging.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pm.authservice.dto.UserDocumentDTO;
import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;

import java.util.List;

public interface OutboxService {
    void createUserEvent(UserJpaEntity user, String type);
    void indexUsersByCreatingUserEvents(List<UserDocumentDTO> documents) throws JsonProcessingException;
}
