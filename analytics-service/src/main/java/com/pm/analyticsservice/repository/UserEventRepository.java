package com.pm.analyticsservice.repository;

import com.pm.analyticsservice.model.UserEventModel;
import com.pm.analyticsservice.model.dto.DailyEventCount;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserEventRepository extends CrudRepository<UserEventModel, UUID> {

    @Modifying
    @Query("INSERT INTO user_events (id, userId, username, email, event_type, event_timestamp) VALUES (:#{#userEvent.id}, :#{#userEvent.userId}, :#{#userEvent.username}, :#{#userEvent.email}, :#{#userEvent.event_type}, :#{#userEvent.event_timestamp})")
    void insert(@Param("userEvent") UserEventModel userEvent);

    @Query("SELECT event_date, event_type, sum(total_events) as total_events FROM analyticsservice.user_events_daily_summary GROUP BY event_date, event_type ORDER BY event_date DESC")
    List<DailyEventCount> getDailyUserSummary();
}
