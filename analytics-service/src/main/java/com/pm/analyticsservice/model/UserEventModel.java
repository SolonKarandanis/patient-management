package com.pm.analyticsservice.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import user.events.UserEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Table("user_events")
public class UserEventModel {
    @Id
    private UUID id;
    private String userId;
    private String username;
    private String email;
    private String event_type;
    private LocalDateTime event_timestamp;


    public static UserEventModel createFromEvent(UserEvent userEvent) {
        UserEventModel userEventModel = new UserEventModel();
        userEventModel.setId(UUID.randomUUID());
        userEventModel.setUserId(userEvent.getUserId());
        userEventModel.setUsername(userEvent.getUsername());
        userEventModel.setEmail(userEvent.getEmail());
        userEventModel.setEvent_type(userEvent.getEventType());
        userEventModel.setEvent_timestamp(LocalDateTime.now());
        return userEventModel;
    }

    private void setId(UUID id) {
        this.id = id;
    }

    private void setUserId(String userId) {
        this.userId = userId;
    }

    private void setUsername(String username) {
        this.username = username;
    }

    private void setEmail(String email) {
        this.email = email;
    }

    private void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    private void setEvent_timestamp(LocalDateTime event_timestamp) {
        this.event_timestamp = event_timestamp;
    }
}
