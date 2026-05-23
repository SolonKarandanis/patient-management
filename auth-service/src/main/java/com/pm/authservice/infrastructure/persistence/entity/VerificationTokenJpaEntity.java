package com.pm.authservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "verification_token")
public class VerificationTokenJpaEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "tokenSequenceGenerator"
    )
    @SequenceGenerator(
            name = "tokenSequenceGenerator",
            sequenceName = "token_generator",
            allocationSize = 1
    )
    @Column(name = "id")
    private Integer id;

    @Column(name = "domain_id")
    private UUID domainId;

    @Column(name = "user_id")
    private Integer userId;

    @OneToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserJpaEntity user;

    @Column(name = "token")
    private String token;

    @Column(name = "expiration_time")
    private Date expirationTime;
}
