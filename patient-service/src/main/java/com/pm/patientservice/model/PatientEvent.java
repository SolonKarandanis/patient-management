package com.pm.patientservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "patient_event")
public class PatientEvent {

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

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PatientStatus status;

    @Column(name = "details")
    private String details;

    @Column(name = "event_created")
    @NotNull
    private LocalDate eventCreated;

    public PatientEvent(Integer patientId, PatientStatus status, String details) {
        this.patientId = patientId;
        this.status = status;
        this.details = details;
        this.eventCreated = LocalDate.now();
        this.publicId = UUID.randomUUID();
    }
}
