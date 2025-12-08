package com.pm.analyticsservice.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table("user_events")
public class UserEvent {
    @Id
    private UUID id;
    private String userId;
    private String username;
    private String email;
    private String event_type;
    private LocalDateTime event_timestamp;
}
