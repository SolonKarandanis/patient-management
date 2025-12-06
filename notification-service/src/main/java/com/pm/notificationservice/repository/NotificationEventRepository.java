package com.pm.notificationservice.repository;

import com.pm.notificationservice.model.NotificationEventEntity;
import com.pm.notificationservice.model.NotificationEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationEventRepository extends JpaRepository<NotificationEventEntity, Long> {
    List<NotificationEventEntity> findByStatus(NotificationEventStatus status);
}
