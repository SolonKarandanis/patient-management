package com.pm.analyticsservice.service;

import notification.events.NotificationEvent;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface KafkaNotificationGateway {

    @Gateway(requestChannel = "notificationChannel")
    void sendNotification(NotificationEvent event);
}
