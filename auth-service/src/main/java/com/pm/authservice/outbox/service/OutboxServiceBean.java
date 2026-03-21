package com.pm.authservice.outbox.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pm.authservice.outbox.model.OutboxEvent;
import com.pm.authservice.outbox.repository.OutboxEventRepository;
import com.pm.authservice.user.dto.UserDTO;
import com.pm.authservice.user.model.UserEntity;
import com.pm.authservice.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional(propagation = Propagation.MANDATORY)
public class OutboxServiceBean implements OutboxService {

    private static final Logger log = LoggerFactory.getLogger(OutboxServiceBean.class);

    private final OutboxEventRepository outboxEventRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public OutboxServiceBean(OutboxEventRepository outboxEventRepository, @Lazy UserService userService) {
        this.outboxEventRepository = outboxEventRepository;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void createEvent(UserEntity user, String type) {
        try {
            // Need the ID to be populated
            if (user.getId() == null) {
               log.warn("Cannot create Outbox Event for User because ID is null (Entity not yet flushed?)");
               return;
            }

            UserDTO userDTO = userService.convertToDTO(user, true);
            String payload = objectMapper.writeValueAsString(userDTO);

            OutboxEvent event = OutboxEvent.builder()
                    .id(UUID.randomUUID())
                    .aggregateType("User")
                    .aggregateId(user.getId().toString())
                    .type(type)
                    .payload(payload)
                    .timestamp(LocalDateTime.now())
                    .build();

            outboxEventRepository.save(event);
            log.debug("Saved OutboxEvent: {} for User: {}", type, user.getId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize Outbox payload for user: {}", user.getId(), e);
        }
    }
}
