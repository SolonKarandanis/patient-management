package com.pm.authservice.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NamedQuery(name = UserEventEntity.FIND_BY_PUBLIC_ID,
        query = "SELECT ue FROM UserEventEntity ue " +
                "WHERE ue.publicId= :publicId ")
@NamedQuery(name = UserEventEntity.FIND_BY_USER_ID,
        query = "SELECT ue FROM UserEventEntity ue " +
                "WHERE ue.userId= :userId ")
@NamedQuery(name = UserEventEntity.FIND_BY_USER_PUBLIC_ID,
        query = "SELECT ue FROM UserEventEntity ue " +
                "WHERE ue.userPublicId= :userPublicId ")
@NamedQuery(name = UserEventEntity.FIND_BY_USERNAME,
        query = "SELECT ue FROM UserEventEntity ue " +
                "WHERE ue.username= :username ")
@NamedQuery(name = UserEventEntity.FIND_BY_EMAIL,
        query = "SELECT ue FROM UserEventEntity ue " +
                "WHERE ue.email= :email ")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_event")
public class UserEventEntity {

    public static final String FIND_BY_PUBLIC_ID= "UserEvent.findByPublicId";
    public static final String FIND_BY_USER_ID= "UserEvent.findByUserId";
    public static final String FIND_BY_USER_PUBLIC_ID= "UserEvent.findByUserPublicId";
    public static final String FIND_BY_USERNAME= "UserEvent.findByUsername";
    public static final String FIND_BY_EMAIL= "UserEvent.findByEmail";

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "userEventGenerator"
    )
    @SequenceGenerator(
            name = "userEventGenerator",
            sequenceName = "user_event_seq",
            allocationSize = 1,
            initialValue = 1
    )
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @NaturalId
    @Column(name = "public_id",nullable = false, updatable = false, unique = true)
    private UUID publicId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "user_public_id",nullable = false, updatable = false, unique = true)
    private UUID userPublicId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "username")
    @NotNull
    private String username;

    @Column(name = "email")
    @Email
    @NotNull
    private String email;

    @Column(name = "event_created")
    @NotNull
    private LocalDate eventCreated;

    public UserEventEntity(Integer userId,UUID userPublicId, UserStatus status, String username,String email) {
        this.userId = userId;
        this.userPublicId = userPublicId;
        this.status = status;
        this.username = username;
        this.email = email;
        this.eventCreated = LocalDate.now();
        this.publicId = UUID.randomUUID();
    }
}
