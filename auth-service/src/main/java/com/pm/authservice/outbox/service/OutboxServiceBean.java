package com.pm.authservice.outbox.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pm.authservice.outbox.model.OutboxEvent;
import com.pm.authservice.outbox.repository.OutboxEventRepository;
import com.pm.authservice.user.model.RoleEntity;
import com.pm.authservice.user.model.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.MANDATORY)
public class OutboxServiceBean implements OutboxService {

    private static final Logger log = LoggerFactory.getLogger(OutboxServiceBean.class);

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public OutboxServiceBean(OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    @Override
    public void createUserEvent(UserEntity user, String type) {
        try {
            if (user.getId() == null) {
               log.warn("Cannot create Outbox Event for User because ID is null (Entity not yet flushed?)");
               return;
            }

            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("id", user.getId());
            payloadMap.put("publicId", user.getPublicId().toString());
            payloadMap.put("username", user.getUsername());
            payloadMap.put("firstName", user.getFirstName());
            payloadMap.put("lastName", user.getLastName());
            payloadMap.put("email", user.getEmail());
            payloadMap.put("status", user.getStatus() != null ? user.getStatus().getValue() : null);
            payloadMap.put("isEnabled", user.getIsEnabled());
            payloadMap.put("isVerified", user.getIsVerified());

            if (user.getRoles() != null) {
                List<String> roleNames = user.getRoles().stream()
                        .map(RoleEntity::getName)
                        .collect(Collectors.toList());
                payloadMap.put("rolesNames", roleNames);

                List<Integer> roleIds = user.getRoles().stream()
                        .map(RoleEntity::getId)
                        .collect(Collectors.toList());
                payloadMap.put("roleIds", roleIds);
            }

            String payload = objectMapper.writeValueAsString(payloadMap);

            OutboxEvent event = OutboxEvent.builder()
                    .id(UUID.randomUUID())
                    .aggregateType(UserEntity.class.getCanonicalName())
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
