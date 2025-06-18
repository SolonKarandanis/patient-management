package com.pm.authservice.model;

import com.pm.authservice.exception.BusinessException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.NaturalId;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NamedQuery(name = UserEntity.FIND_BY_EMAIL,
        query = "SELECT u FROM UserEntity u "
                + "LEFT JOIN FETCH u.roles r "
                + "WHERE u.email = :email ")
@NamedQuery(name = UserEntity.FIND_BY_PUBLIC_ID,
        query = "SELECT u FROM UserEntity u "
                + "LEFT JOIN FETCH u.roles r " +
                "WHERE u.publicId= :publicId ")
@NamedQuery(name = UserEntity.FIND_BY_USERNAME,
        query = "SELECT u FROM UserEntity u "
                + "LEFT JOIN FETCH u.roles r " +
                " WHERE u.username= :username ")
@NamedQuery(name = UserEntity.FIND_ID_BY_PUBLIC_ID,
        query = "SELECT u.id FROM UserEntity u " +
                "WHERE u.publicId=:publicId")
@NamedEntityGraph(name = UserEntity.GRAPH_USERS_ROLES,
        attributeNodes = @NamedAttributeNode("roles")
)
@Entity
@Table(name="users")
public class UserEntity {
    public static final String FIND_BY_EMAIL= "User.findByEmail";
    public static final String FIND_BY_PUBLIC_ID= "User.findByPublicId";
    public static final String FIND_BY_USERNAME= "User.findByUsername";
    public static final String FIND_ID_BY_PUBLIC_ID= "User.findIdByPublicId";

    public static final String GRAPH_USERS_ROLES="graph.users.roles";

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "userGenerator"
    )
    @SequenceGenerator(
            name = "userGenerator",
            sequenceName = "users_seq",
            allocationSize = 1,
            initialValue = 1
    )
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @NaturalId
    @Column(name = "public_id",nullable = false, updatable = false, unique = true)
    private UUID publicId;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "status")
    private AccountStatus status;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    @NaturalId
    @NotNull
    @Email
    @Column(unique = true, name = "email")
    private String email;

    @Column(name = "created_date")
    @NotNull
    private LocalDate createdDate;

    @Column(name = "last_modified_date")
    @NotNull
    private LocalDate lastModifiedDate;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    },fetch = FetchType.LAZY)
    @JoinTable(name="user_roles",
            joinColumns=@JoinColumn(name="user_id"),
            inverseJoinColumns=@JoinColumn(name="role_id"))
    @BatchSize(size = 50) // Batch loading strategy
    private Set<RoleEntity> roles= new HashSet<>();

    public void addRole(RoleEntity role) {
        this.roles.add(role);
    }

    public void removeRole(RoleEntity role) {
        this.roles.remove(role);
    }

    public void removeRoles() {
        Iterator<RoleEntity> iterator =this.roles.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    //Domain Logic to be moved to UserDomain
    public void activate() throws BusinessException {
        if(AccountStatus.ACTIVE.equals(this.status)){
            throw new BusinessException("error.user.already.active");
        }
        setStatus(AccountStatus.ACTIVE);
        setIsEnabled(Boolean.TRUE);
        setIsVerified(Boolean.TRUE);
    }

    public void deactivate() throws BusinessException {
        if(AccountStatus.INACTIVE.equals(this.status)){
            throw new BusinessException("error.user.already.inactive");
        }
        setStatus(AccountStatus.INACTIVE);
        setIsEnabled(Boolean.FALSE);
    }
}
