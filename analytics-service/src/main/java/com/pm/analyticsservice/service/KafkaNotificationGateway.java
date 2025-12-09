package com.pm.analyticsservice.service;

import com.pm.analyticsservice.config.AppConstants;
import notification.events.NotificationEvent;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface KafkaNotificationGateway {

    @Gateway(requestChannel = AppConstants.NOTIFICATION_CHANNEL)
    void sendNotification(NotificationEvent event);
}
