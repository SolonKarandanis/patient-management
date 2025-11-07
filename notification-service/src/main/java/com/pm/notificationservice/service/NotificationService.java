package com.pm.notificationservice.service;

import notification.events.NotificationEvent;

public interface NotificationService {
    void sendNotification(NotificationEvent notificationEvent);
}
