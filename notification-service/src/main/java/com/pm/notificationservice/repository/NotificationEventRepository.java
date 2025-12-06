package com.pm.notificationservice.repository;

import com.pm.notificationservice.model.NotificationEventEntity;
import com.pm.notificationservice.model.NotificationEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationEventRepository extends JpaRepository<NotificationEventEntity, Long> {

    @Query(name = NotificationEventEntity.FIND_BY_STATUS)
    List<NotificationEventEntity> findByStatus(NotificationEventStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(name = NotificationEventEntity.FIND_BY_ID_WITH_LOCK)
    Optional<NotificationEventEntity> findByIdWithLock(@Param("id") Long id);

    @Modifying
    @Query(name = NotificationEventEntity.UPDATE_STATUS_BY_ID)
    int updateStatusById(@Param("id") Long id, @Param("status") NotificationEventStatus status);
}
