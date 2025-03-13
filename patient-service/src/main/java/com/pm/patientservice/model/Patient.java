package com.pm.patientservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "patient")
@NamedQuery(name = Patient.EXISTS_BY_EMAIL_AND_NOT_PUBLIC_ID,
        query = "SELECT CASE WHEN COUNT(patient) > 0 THEN TRUE ELSE FALSE END " +
                "FROM Patient  patient "+
                "WHERE patient.email = :email " +
                "AND patient.publicId != :publicId")
@NamedQuery(name = Patient.FIND_BY_PUBLIC_ID,
        query = "SELECT patient FROM Patient patient "
                + "WHERE patient.publicId= :publicId ")
public class Patient {
    public static final String EXISTS_BY_EMAIL_AND_NOT_PUBLIC_ID= "Patient.existsByEmailAndNotPublicId";
    public static final String FIND_BY_PUBLIC_ID= "Patient.findByPublicId";

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "patientGenerator"
    )
    @SequenceGenerator(
            name = "patientGenerator",
            sequenceName = "patient_seq",
            allocationSize = 1,
            initialValue = 1
    )
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @NaturalId
    @Column(name = "public_id",nullable = false, updatable = false, unique = true)
    private UUID publicId;

    @Column(name = "name")
    @NotNull
    private String name;

    @NaturalId
    @NotNull
    @Email
    @Column(unique = true, name = "email")
    private String email;

    @Column(name = "address")
    @NotNull
    private String address;

    @Column(name = "date_of_birth")
    @NotNull
    private LocalDate dateOfBirth;

    @Column(name = "registered_date")
    @NotNull
    private LocalDate registeredDate;
}
