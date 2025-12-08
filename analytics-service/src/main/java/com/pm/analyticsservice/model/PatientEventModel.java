package com.pm.analyticsservice.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import patient.events.PatientEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Table("patient_events")
public class PatientEventModel {
    @Id
    private UUID id;
    private String patientId;
    private String name;
    private String email;
    private String event_type;
    private LocalDateTime event_timestamp;

    public static PatientEventModel createFromEvent(PatientEvent patientEvent) {
        PatientEventModel patientEventModel = new PatientEventModel();
        patientEventModel.setId(UUID.randomUUID());
        patientEventModel.setPatientId(patientEvent.getPatientId());
        patientEventModel.setName(patientEvent.getName());
        patientEventModel.setEmail(patientEvent.getEmail());
        patientEventModel.setEvent_type(patientEventModel.getEvent_type());
        patientEventModel.setEvent_timestamp(LocalDateTime.now());
        return patientEventModel;
    }

    private void  setId(UUID id) {
        this.id = id;
    }

    private void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setEmail(String email) {
        this.email = email;
    }

    private void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    private void setEvent_timestamp(LocalDateTime event_timestamp) {
        this.event_timestamp = event_timestamp;
    }
}
