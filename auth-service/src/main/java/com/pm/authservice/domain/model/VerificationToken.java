package com.pm.authservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationToken {
    private static final int EXPIRATION_MINUTES = 15;

    private UUID domainId;
    private String token;
    private UUID userDomainId;
    private Date expirationTime;

    public static VerificationToken create(String token, UUID userDomainId) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, EXPIRATION_MINUTES);
        return VerificationToken.builder()
                .domainId(UUID.randomUUID())
                .token(token)
                .userDomainId(userDomainId)
                .expirationTime(new Date(cal.getTimeInMillis()))
                .build();
    }

    public boolean isExpired() {
        return expirationTime != null && expirationTime.before(new Date());
    }
}
