package com.pm.authservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationToken {
    private String token;
    private UUID userDomainId;
    private Date expirationTime;

    public boolean isExpired() {
        return expirationTime != null && expirationTime.before(new Date());
    }
}
