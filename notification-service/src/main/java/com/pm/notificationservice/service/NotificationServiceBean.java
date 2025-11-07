package com.pm.notificationservice.service;

import com.pm.notificationservice.dto.NotificationDTO;
import lombok.extern.slf4j.Slf4j;
import notification.events.NotificationEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationServiceBean implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationServiceBean(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotification(NotificationEvent notificationEvent) {
        notificationEvent.getUserIdsList().forEach(userId -> {
            log.info("Sending WS notification to {} with payload {}", userId, notificationEvent.getTitle());
            NotificationDTO dto = new NotificationDTO(notificationEvent.getTitle(), notificationEvent.getMessage());
            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + userId,
                    dto
            );
        });

    }
}
