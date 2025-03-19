package com.pm.authservice.model;

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
@NamedEntityGraph(name = User.GRAPH_USERS_ROLES,
        attributeNodes = @NamedAttributeNode("roles")
)
@Entity
@Table(name="users")
public class User {

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

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    },fetch = FetchType.LAZY)
    @JoinTable(name="user_roles",
            joinColumns=@JoinColumn(name="user_id"),
            inverseJoinColumns=@JoinColumn(name="role_id"))
    @BatchSize(size = 50) // Batch loading strategy
    private Set<Role> roles= new HashSet<>();

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public void removeRoles() {
        Iterator<Role> iterator =this.roles.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }
}
