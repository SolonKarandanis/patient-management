package com.pm.authservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NamedQuery(name = RoleEntity.FIND_BY_IDS,
        query = "SELECT r FROM RoleEntity r WHERE r.id in (:ids)")
@NamedQuery(name = RoleEntity.FIND_BY_NAME,
        query = "SELECT r FROM RoleEntity r " +
                "WHERE r.name = :name")
@Entity
@Table(name="roles")
public class RoleEntity {

    public static final String FIND_BY_IDS= "Role.findByIds";
    public static final String FIND_BY_NAME= "User.findByName";

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

    //bidirectional many-to-one association to RoleOperation
    @OneToMany(mappedBy="role", fetch=FetchType.LAZY )
    private List<RoleOperationEntity> roleOperations;
}
