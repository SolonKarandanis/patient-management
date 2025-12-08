package com.pm.analyticsservice.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Table("notification_events")
public class NotificationEvent {
    @Id
    private UUID id;
    private List<String> userIds;
    private String title;
    private String eventType;
    private String message;
    private LocalDateTime event_timestamp;
}
