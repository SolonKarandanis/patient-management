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
import java.util.List;
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

    private NotificationEventEntity createNotificationEvent(NotificationEvent notificationEvent) {
        NotificationEventEntity notificationEntity = NotificationEventEntity.builder()
                .userIds(notificationEvent.getUserIdsList())
                .title(notificationEvent.getTitle())
                .eventType(notificationEvent.getEventType())
                .message(notificationEvent.getMessage())
                .status(NotificationEventStatus.NOTIFICATION_EVENT_CREATED)
                .createdDate(LocalDateTime.now())
                .build();
        log.info("Saving Notification to database with eventType:{}", notificationEntity.getEventType());
        notificationEntity=notificationEntityService.saveNotificationEvent(notificationEntity);
        log.info("Notification saved");
        return notificationEntity;
    }

    public void handleNotification(NotificationEvent notificationEvent) {
        NotificationEventEntity notificationEntity=createNotificationEvent(notificationEvent);
        String eventType = notificationEntity.getEventType();

        sendImmediately(notificationEntity);
    }

    private void sendImmediately(NotificationEventEntity notificationEntity) {
        List<String> userIdsList= notificationEntity.getUserIds();
        if(!CollectionUtils.isEmpty(notificationEntity.getUserIds())){
            userIdsList.forEach(userId -> {
                log.info("Sending {} notification to {} with payload {}", notificationEntity.getEventType(),userId, notificationEntity.getTitle());
                NotificationDTO dto = new NotificationDTO(notificationEntity.getTitle(), notificationEntity.getMessage(),notificationEntity.getEventType());
                String destination = "/topic/notifications/" + userId;
                sendNotification(dto,destination);
            });
        }
        else{
            log.info("Sending {} notification  with payload {}", notificationEntity.getEventType(), notificationEntity.getTitle());
            NotificationDTO dto = new NotificationDTO(notificationEntity.getEventType());
            String destination = "/topic/notifications";
            sendNotification(dto,destination);
        }
    }

    private void sendNotification(NotificationDTO dto, String destination){
        MessageHeaders headers = new MessageHeaders(Map.of(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON));
        try {
            String payload = objectMapper.writeValueAsString(dto);
            messagingTemplate.convertAndSend(
                    destination,
                    payload,
                    headers
            );
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize notification DTO to destination: {}",destination, e);
        }
    }
}
