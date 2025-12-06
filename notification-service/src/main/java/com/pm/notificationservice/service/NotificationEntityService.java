package com.pm.notificationservice.service;

import com.pm.notificationservice.model.NotificationEventEntity;
import com.pm.notificationservice.model.NotificationEventStatus;

import java.util.List;

public interface NotificationEntityService {
    NotificationEventEntity saveNotificationEvent(NotificationEventEntity notificationEventEntity);
    NotificationEventEntity getNotificationEvent(Long id);
    List<NotificationEventEntity> findByStatus(NotificationEventStatus status);
}
