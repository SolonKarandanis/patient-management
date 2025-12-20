package com.pm.notificationservice.notification.service;

import com.pm.notificationservice.notification.model.NotificationEventEntity;
import notification.events.NotificationEvent;

public interface NotificationService {
    void handleNotificationFromBroker(NotificationEvent notificationEvent);
    void sendNotification(NotificationEventEntity notificationEntity);

}
