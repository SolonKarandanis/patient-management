package com.pm.notificationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "notification_event")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEventEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "notificationEventGenerator"
    )
    @SequenceGenerator(
            name = "notificationEventGenerator",
            sequenceName = "notification_event_seq",
            allocationSize = 1,
            initialValue = 1
    )
    private Long id;

    @Column(name = "user_ids", columnDefinition = "TEXT[]")
    private List<String> userIds;

    private String title;
    private String eventType;
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationEventStatus status;

    private LocalDateTime createdDate;
    private LocalDateTime sentDate;
}