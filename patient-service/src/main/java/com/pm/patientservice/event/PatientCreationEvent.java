package com.pm.patientservice.event;

import com.pm.patientservice.model.Patient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class PatientCreationEvent extends ApplicationEvent {
    private Patient newPatient;

    public PatientCreationEvent(Patient newPatient) {
        super(newPatient);
        this.newPatient = newPatient;
    }
}
