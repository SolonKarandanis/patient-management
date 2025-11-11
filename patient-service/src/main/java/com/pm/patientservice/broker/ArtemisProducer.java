package com.pm.patientservice.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.patientservice.dto.PatientEventDTO;
import com.pm.patientservice.model.PatientEventEntity;
import jakarta.jms.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class ArtemisProducer implements Producer<PatientEventEntity>{

    private static final Logger LOGGER = LoggerFactory.getLogger(ArtemisProducer.class);

    private final JmsTemplate jmsTemplate;
    private final Queue patientQueue;
    private final ObjectMapper objectMapper;

    public ArtemisProducer(
            JmsTemplate jmsTemplate,
            @Qualifier("patientQueue") Queue patientQueue,
            ObjectMapper objectMapper
    ) {
        this.jmsTemplate = jmsTemplate;
        this.patientQueue = patientQueue;
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendEvent(PatientEventEntity object) {
        PatientEventDTO dto = new PatientEventDTO();
        dto.setPublicId(object.getPublicId().toString());
        dto.setPatientId(object.getPatientId().toString());
        dto.setName(object.getName());
        dto.setEmail(object.getEmail());
        try {
            String message = objectMapper.writeValueAsString(dto);
            LOGGER.info("Sending message to queue: {}", message);
            jmsTemplate.convertAndSend(patientQueue, message);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error converting PatientEventEntity to JSON", e);
        }
    }
}
