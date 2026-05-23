package com.pm.authservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "role_operations")
public class RoleOperationJpaEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private RoleOperationsPK roleOperationsPK;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "operation_id", insertable = false, updatable = false)
    private OperationJpaEntity operation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private RoleJpaEntity role;
}
