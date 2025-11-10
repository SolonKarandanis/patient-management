package com.pm.patientservice.event;

import com.pm.patientservice.broker.ArtemisProducer;
import com.pm.patientservice.broker.KafkaProducer;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.model.PatientEventEntity;
import com.pm.patientservice.model.PatientStatus;
import com.pm.patientservice.repository.PatientEventRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PatientCreationEventListener implements ApplicationListener<PatientCreationEvent> {

    private final PatientEventRepository patientEventRepository;
    private final KafkaProducer kafkaProducer;
    private final ArtemisProducer artemisProducer;

    public PatientCreationEventListener(
            PatientEventRepository patientEventRepository,
            KafkaProducer kafkaProducer,
            ArtemisProducer artemisProducer
    ) {
        this.patientEventRepository = patientEventRepository;
        this.kafkaProducer = kafkaProducer;
        this.artemisProducer = artemisProducer;
    }

    @Override
    public void onApplicationEvent(PatientCreationEvent event) {
        Patient patient = event.getNewPatient();
        PatientEventEntity patientEventEntity = new PatientEventEntity(
                patient.getId(),
                patient.getPublicId(),
                PatientStatus.PATIENT_CREATED,
                "Patient created successfully",
                patient.getName(),
                patient.getEmail());
        saveAndPublishEvents(patientEventEntity);
    }

    private void saveAndPublishEvents(PatientEventEntity patientEvent){
        patientEventRepository.save(patientEvent);
        kafkaProducer.sendEvent(patientEvent);
        artemisProducer.sendEvent(patientEvent);
    }
}
