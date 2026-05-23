package com.pm.authservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NamedQuery(name = RoleJpaEntity.FIND_BY_IDS,
        query = "SELECT r FROM RoleJpaEntity r WHERE r.id in (:ids)")
@NamedQuery(name = RoleJpaEntity.FIND_BY_NAME,
        query = "SELECT r FROM RoleJpaEntity r WHERE r.name = :name")
@Entity
@Table(name = "roles")
public class RoleJpaEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String FIND_BY_IDS = "Role.findByIds";
    public static final String FIND_BY_NAME = "User.findByName";

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "roleGenerator"
    )
    @SequenceGenerator(
            name = "roleGenerator",
            sequenceName = "roles_seq",
            allocationSize = 1,
            initialValue = 1
    )
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<RoleOperationJpaEntity> roleOperations;
}
