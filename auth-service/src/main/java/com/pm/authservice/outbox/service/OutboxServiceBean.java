package com.pm.authservice.outbox.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pm.authservice.outbox.model.OutboxEvent;
import com.pm.authservice.outbox.repository.OutboxEventRepository;
import com.pm.authservice.dto.UserDocumentDTO;
import com.pm.authservice.service.GenericService;
import com.pm.authservice.user.model.UserEntity;
import com.pm.authservice.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(propagation = Propagation.MANDATORY)
public class OutboxServiceBean implements OutboxService {

    private static final Logger log = LoggerFactory.getLogger(OutboxServiceBean.class);

    private final OutboxEventRepository outboxEventRepository;
    private final GenericService genericService;
    private final ObjectMapper objectMapper;

    public OutboxServiceBean(OutboxEventRepository outboxEventRepository, GenericService genericService) {
        this.outboxEventRepository = outboxEventRepository;
        this.genericService = genericService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    protected OutboxEvent convertToOutboxEvent(Integer userId,String type,String payload){
        return OutboxEvent.builder()
                .id(UUID.randomUUID())
                .aggregateType(UserEntity.class.getCanonicalName())
                .aggregateId(userId.toString())
                .type(type)
                .payload(payload)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public void createUserEvent(UserEntity user, String type) {
        try {
            if (user.getId() == null) {
               log.warn("Cannot create Outbox Event for User because ID is null (Entity not yet flushed?)");
               return;
            }
            UserDocumentDTO dto = genericService.convertToDocumentDto(user);
            String payload = objectMapper.writeValueAsString(dto);
            OutboxEvent event = convertToOutboxEvent(user.getId(),type,payload);
            outboxEventRepository.save(event);
            log.debug("Saved OutboxEvent: {} for User: {}", type, user.getId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize Outbox payload for user: {}", user.getId(), e);
        }
    }

    @Override
    public void indexUsersByCreatingUserEvents(List<UserDocumentDTO> documents) throws JsonProcessingException {
        List<OutboxEvent> events = new ArrayList<>();
        for(UserDocumentDTO document : documents){
            String payload = objectMapper.writeValueAsString(document);
            OutboxEvent event = convertToOutboxEvent(document.getId(), AppConstants.OUTBOX_USER_UPDATED,payload);
            events.add(event);
        }
        outboxEventRepository.saveAll(events);
    }
}
