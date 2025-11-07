package com.pm.notificationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.notificationservice.dto.NotificationDTO;
import lombok.extern.slf4j.Slf4j;
import notification.events.NotificationEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.MimeTypeUtils;
import java.util.Map;


@Service
@Slf4j
public class NotificationServiceBean implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public NotificationServiceBean(SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendNotification(NotificationEvent notificationEvent) {
        notificationEvent.getUserIdsList().forEach(userId -> {
            log.info("Sending WS notification to {} with payload {}", userId, notificationEvent.getTitle());
            NotificationDTO dto = new NotificationDTO(notificationEvent.getTitle(), notificationEvent.getMessage());
            try {
                String payload = objectMapper.writeValueAsString(dto);
                MessageHeaders headers = new MessageHeaders(Map.of(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON));
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
}
