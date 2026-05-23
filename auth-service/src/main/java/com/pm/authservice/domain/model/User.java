package com.pm.authservice.domain.model;

import com.pm.authservice.domain.exception.BusinessRuleException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class User {
    private UUID domainId;
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private AccountStatus status;
    private Boolean isEnabled;
    private Boolean isVerified;
    private LocalDate createdDate;
    private LocalDate lastModifiedDate;
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    public boolean hasRole(String roleName) {
        return roles.stream().anyMatch(r -> roleName.equals(r.getName()));
    }

    public boolean isSystemAdmin() {
        return hasRole(AuthorityConstants.ROLE_SYSTEM_ADMIN);
    }

    public void activate() {
        if (AccountStatus.ACTIVE.equals(this.status)) {
            throw new BusinessRuleException("error.user.already.active");
        }
        this.status = AccountStatus.ACTIVE;
        this.isEnabled = Boolean.TRUE;
        this.isVerified = Boolean.TRUE;
    }

    public void deactivate() {
        if (AccountStatus.INACTIVE.equals(this.status)) {
            throw new BusinessRuleException("error.user.already.inactive");
        }
        this.status = AccountStatus.INACTIVE;
        this.isEnabled = Boolean.FALSE;
    }
}
