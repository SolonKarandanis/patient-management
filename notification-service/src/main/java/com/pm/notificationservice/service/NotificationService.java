package com.pm.notificationservice.service;

import com.pm.notificationservice.model.NotificationEventEntity;
import notification.events.NotificationEvent;

public interface NotificationService {
    void handleNotificationFromBroker(NotificationEvent notificationEvent);
    void sendNotification(NotificationEventEntity notificationEntity);

}
