package com.pm.authservice.outbox.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pm.authservice.outbox.model.OutboxEvent;
import com.pm.authservice.outbox.repository.OutboxEventRepository;
import com.pm.authservice.user.dto.UserDocumentDTO;
import com.pm.authservice.user.model.RoleEntity;
import com.pm.authservice.user.model.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
            UserDocumentDTO dto = new UserDocumentDTO();
            dto.setId(user.getId());
            dto.setPublicId(user.getPublicId().toString());
            dto.setUsername(user.getUsername());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setEmail(user.getEmail());
            dto.setStatus(user.getStatus() != null ? user.getStatus().getValue() : null);
            dto.setIsVerified(user.getIsVerified());
            dto.setIsEnabled(user.getIsEnabled());

            if (user.getRoles() != null) {
                List<String> roleNames = user.getRoles().stream()
                        .map(RoleEntity::getName)
                        .collect(Collectors.toList());
                dto.setRolesNames(roleNames);

                List<Integer> roleIds = user.getRoles().stream()
                        .map(RoleEntity::getId)
                        .collect(Collectors.toList());
                dto.setRoleIds(roleIds);
            }

            String payload = objectMapper.writeValueAsString(dto);

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
