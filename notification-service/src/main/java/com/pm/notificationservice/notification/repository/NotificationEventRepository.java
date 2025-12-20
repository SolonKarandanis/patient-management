package com.pm.notificationservice.notification.repository;

import com.pm.notificationservice.notification.model.NotificationEventEntity;
import com.pm.notificationservice.notification.model.NotificationEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface NotificationEventRepository extends JpaRepository<NotificationEventEntity, Long> {

    Page<NotificationEventEntity> findByStatus(NotificationEventStatus status, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(name = NotificationEventEntity.FIND_BY_ID_WITH_LOCK)
    Optional<NotificationEventEntity> findByIdWithLock(@Param("id") Long id);

    @Modifying
    @Query(name = NotificationEventEntity.UPDATE_STATUS_BY_ID)
    int updateStatusById(@Param("id") Long id, @Param("status") NotificationEventStatus status);
}
