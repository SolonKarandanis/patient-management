package com.pm.notificationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@NamedQuery(name = NotificationEventEntity.FIND_BY_ID_WITH_LOCK,
        query = "SELECT ne " +
                "FROM NotificationEventEntity ne " +
                "WHERE ne.id = :id")
@NamedQuery(name = NotificationEventEntity.UPDATE_STATUS_BY_ID,
        query = "UPDATE NotificationEventEntity n SET n.status = :status WHERE n.id = :id")
@Entity
@Table(name = "notification_event")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEventEntity {

    public static final String FIND_BY_STATUS= "NotificationEventEntity.findByStatus";
    public static final String FIND_BY_ID_WITH_LOCK = "NotificationEventEntity.findByIdWithLock";
    public static final String UPDATE_STATUS_BY_ID = "NotificationEventEntity.updateStatusById";

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