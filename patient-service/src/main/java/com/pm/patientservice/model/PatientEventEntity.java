package com.pm.patientservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "patient_event")
public class PatientEventEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "patientEventGenerator"
    )
    @SequenceGenerator(
            name = "patientEventGenerator",
            sequenceName = "patient_event_seq",
            allocationSize = 1,
            initialValue = 1
    )
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @NaturalId
    @Column(name = "public_id",nullable = false, updatable = false, unique = true)
    private UUID publicId;

    @Column(name = "patient_id")
    private Integer patientId;

    @Column(name = "patient_public_id",nullable = false, updatable = false, unique = true)
    private UUID patientPublicId;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "email")
    @Email
    @NotNull
    private String email;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PatientStatus status;

    @Column(name = "details")
    private String details;

    @Column(name = "event_created")
    @NotNull
    private LocalDate eventCreated;

    public PatientEventEntity(Integer patientId,UUID patientPublicId, PatientStatus status, String details, String name,String email) {
        this.patientId = patientId;
        this.patientPublicId = patientPublicId;
        this.status = status;
        this.details = details;
        this.name = name;
        this.email = email;
        this.eventCreated = LocalDate.now();
        this.publicId = UUID.randomUUID();
    }
}
