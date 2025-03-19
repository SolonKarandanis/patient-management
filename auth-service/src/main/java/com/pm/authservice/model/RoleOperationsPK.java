package com.pm.authservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class RoleOperationsPK {

    @Column(name = "role_id", nullable = false)
    private Integer rolesId;

    @Column(name = "operation_id", nullable = false)
    private Integer operationId;
}
