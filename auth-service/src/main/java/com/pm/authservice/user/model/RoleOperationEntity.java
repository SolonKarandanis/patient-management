package com.pm.authservice.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name="role_operations")
public class RoleOperationEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private RoleOperationsPK roleOperationsPK;

    //bidirectional many-to-one association to Users
    @ManyToOne(fetch=FetchType.LAZY,optional = false)
    @JoinColumn(name="operation_id",insertable=false, updatable=false)
    private OperationEntity operation;

    //bidirectional many-to-one association to Role
    @ManyToOne(fetch=FetchType.LAZY,optional = false)
    @JoinColumn(name="role_id" ,insertable=false, updatable=false)
    private RoleEntity role;
}
