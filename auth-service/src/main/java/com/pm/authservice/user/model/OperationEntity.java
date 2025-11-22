package com.pm.authservice.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="operations")
public class OperationEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "operationGenerator"
    )
    @SequenceGenerator(
            name = "operationGenerator",
            sequenceName = "operations_seq",
            allocationSize = 1,
            initialValue = 1
    )
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    //bidirectional many-to-one association to RoleOperation
    @OneToMany(mappedBy="operation", fetch=FetchType.LAZY )
    private List<RoleOperationEntity> roleOperations;
}
