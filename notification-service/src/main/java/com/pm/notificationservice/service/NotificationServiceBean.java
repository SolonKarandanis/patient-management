package com.pm.notificationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ProtocolStringList;
import com.pm.notificationservice.dto.NotificationDTO;
import com.pm.notificationservice.model.NotificationEventEntity;
import com.pm.notificationservice.model.NotificationEventStatus;
import lombok.extern.slf4j.Slf4j;
import notification.events.NotificationEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MimeTypeUtils;

import java.time.LocalDateTime;
import java.util.Map;


@Service
@Slf4j
public class NotificationServiceBean implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private final NotificationEntityService notificationEntityService;

    public NotificationServiceBean(
            SimpMessagingTemplate messagingTemplate,
            ObjectMapper objectMapper,
            NotificationEntityService notificationEntityService
    ) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
        this.notificationEntityService = notificationEntityService;
    }

    private void createNotificationEvent(NotificationEvent notificationEvent) {
        NotificationEventEntity toBeSaved = NotificationEventEntity.builder()
                .userIds(notificationEvent.getUserIdsList())
                .title(notificationEvent.getTitle())
                .eventType(notificationEvent.getEventType())
                .message(notificationEvent.getMessage())
                .status(NotificationEventStatus.NOTIFICATION_EVENT_CREATED)
                .createdDate(LocalDateTime.now())
                .build();
        notificationEntityService.saveNotificationEvent(toBeSaved);
    }

    public void sendNotification(NotificationEvent notificationEvent) {
        createNotificationEvent(notificationEvent);
        ProtocolStringList userIdsList = notificationEvent.getUserIdsList();
        MessageHeaders headers = new MessageHeaders(Map.of(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON));
        if(!CollectionUtils.isEmpty(userIdsList)){
            userIdsList.forEach(userId -> {
                log.info("Sending {} notification to {} with payload {}", notificationEvent.getEventType(),userId, notificationEvent.getTitle());
                NotificationDTO dto = new NotificationDTO(notificationEvent.getTitle(), notificationEvent.getMessage(),notificationEvent.getEventType());
                try {
                    String payload = objectMapper.writeValueAsString(dto);
                    messagingTemplate.convertAndSend(
                            "/topic/notifications/" + userId,
                            payload,
                            headers
                    );
                } catch (JsonProcessingException e) {
                    log.error("Failed to serialize notification DTO for user {}", userId, e);
                }
            });
        }
        else{
            log.info("Sending {} notification  with payload {}", notificationEvent.getEventType(), notificationEvent.getTitle());
            NotificationDTO dto = new NotificationDTO(notificationEvent.getEventType());
            try {
                String payload = objectMapper.writeValueAsString(dto);
                messagingTemplate.convertAndSend(
                        "/topic/notifications",
                        payload,
                        headers
                );
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize notification DTO", e);
            }
        }
    }
}
