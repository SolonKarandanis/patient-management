package com.pm.notificationservice.service;

import com.pm.notificationservice.model.NotificationEventEntity;
import com.pm.notificationservice.model.NotificationEventStatus;
import com.pm.notificationservice.repository.NotificationEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@Slf4j
public class NotificationEntityServiceBean implements NotificationEntityService {

    private final NotificationEventRepository notificationEventRepository;

    public NotificationEntityServiceBean(NotificationEventRepository notificationEventRepository) {
        this.notificationEventRepository = notificationEventRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public NotificationEventEntity saveNotificationEvent(NotificationEventEntity notificationEventEntity) {
        return notificationEventRepository.save(notificationEventEntity);
    }

    @Override
    public NotificationEventEntity getNotificationEvent(Long id) {
        return notificationEventRepository
                .findById(id)
                .orElse(null);
    }

    @Override
    public List<NotificationEventEntity> findByStatus(NotificationEventStatus status) {
        return notificationEventRepository.findByStatus(status);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateNotificationStatus(Long id, NotificationEventStatus status) {
        notificationEventRepository.findByIdWithLock(id)
                .ifPresent(notificationEventEntity -> {
                    if (status == NotificationEventStatus.NOTIFICATION_EVENT_SENT) {
                        notificationEventEntity.setSentDate(LocalDateTime.now());
                    }
                    notificationEventEntity.setStatus(status);
                    log.info("Updating Notification to database with eventType:{} and status: {}", notificationEventEntity.getEventType(),notificationEventEntity.getStatus());
                    notificationEventRepository.save(notificationEventEntity);
                    log.info("Notification updated");
                });
    }
}
