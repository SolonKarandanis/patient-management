package com.pm.authservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
@NamedQuery(name = VerificationTokenEntity.FIND_BY_TOKEN,
        query = "SELECT vt FROM VerificationTokenEntity vt " +
                "LEFT JOIN FETCH vt.user u " +
                "WHERE vt.token= :token")
@Entity
@Table(name = "verification_token")
public class VerificationTokenEntity {

    public static final String FIND_BY_TOKEN= "VerificationToken.findByToken";

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

    @Column(name = "user_id")
    private Integer userId;

    @OneToOne
    @JoinColumn(name = "user_id",insertable=false, updatable=false)
    private UserEntity user;

    @Column(name = "token")
    private String token;

    @Column(name = "expiration_time")
    private Date expirationTime;

    private static final int EXPIRATION_TIME = 15;

    public Boolean isTokenExpired(){
        Date now = new Date();
        if(this.expirationTime.before(now)){
            return true;
        }
        return false;
    }

    public VerificationTokenEntity(){

    }

    public VerificationTokenEntity(String token, UserEntity user) {
        super();
        this.token = token;
        this.user = user;
        this.userId= user.getId();
        this.expirationTime = this.getTokenExpirationTime();
    }

    public VerificationTokenEntity(String token) {
        super();
        this.token = token;
        this.expirationTime = this.getTokenExpirationTime();
    }

    public Date getTokenExpirationTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, EXPIRATION_TIME);
        return new Date(calendar.getTime().getTime());
    }

}
