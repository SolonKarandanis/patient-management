package com.pm.notificationservice.notification.service;

import com.pm.notificationservice.notification.model.NotificationEventEntity;
import com.pm.notificationservice.notification.model.NotificationEventStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationEntityService {
    NotificationEventEntity saveNotificationEvent(NotificationEventEntity notificationEventEntity);
    NotificationEventEntity getNotificationEvent(Long id);
    Page<NotificationEventEntity> findByStatus(NotificationEventStatus status, Pageable pageable);
    void updateNotificationStatus(Long id, NotificationEventStatus status);
}
