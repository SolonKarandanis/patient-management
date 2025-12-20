package com.pm.notificationservice.email.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "email_types")
public class EmailType {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "resource_key")
    private String resourceKey;

    //bi-directional many-to-one association to Email
    @OneToMany(mappedBy = "emailType")
    private List<Email> emails;


}
