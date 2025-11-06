package com.pm.authservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class RoleOperationsPK implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "role_id", nullable = false)
    private Integer rolesId;

    @Column(name = "operation_id", nullable = false)
    private Integer operationId;
}
