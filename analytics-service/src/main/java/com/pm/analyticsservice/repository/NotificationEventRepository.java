package com.pm.analyticsservice.repository;

import com.pm.analyticsservice.domain.NotificationEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationEventRepository extends CrudRepository<NotificationEvent, UUID> {
}
