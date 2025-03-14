package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PatientServiceBean implements PatientService{
    private static final Logger log = LoggerFactory.getLogger(PatientServiceBean.class);
    protected static final String PATIENT_NOT_FOUND="error.patient.not.found";
    protected static final String PATIENT_WITH_EMAIL_EXISTS="error.patient.email.exists";

    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
//    private final KafkaProducer kafkaProducer;


    public PatientServiceBean(
            PatientRepository patientRepository,
            BillingServiceGrpcClient billingServiceGrpcClient
    ) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
    }

    @Override
    public List<PatientResponseDTO> getPatients() {
        return patientRepository.findAll().stream().map(this::convertToDTO).toList();
    }

    @Override
    public Patient getPatientById(String id) throws PatientNotFoundException {
        return patientRepository.findOneByPublicId(UUID.fromString(id)).orElseThrow(
                () -> new PatientNotFoundException(PATIENT_NOT_FOUND));
    }

    @Transactional
    @Override
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) throws EmailAlreadyExistsException {
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException(PATIENT_WITH_EMAIL_EXISTS);
        }
        Patient newPatient =convertToEntity(patientRequestDTO);
        UUID uuid = UUID.randomUUID();
        newPatient.setPublicId(uuid);
        newPatient = patientRepository.save(newPatient);

        billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(),
                newPatient.getName(), newPatient.getEmail());
//
//        kafkaProducer.sendEvent(newPatient);
        return convertToDTO(newPatient);
    }

    @Transactional
    @Override
    public PatientResponseDTO updatePatient(String id, PatientRequestDTO patientRequestDTO)
            throws EmailAlreadyExistsException, PatientNotFoundException {
        Patient patient = getPatientById(id);

        if (patientRepository.patientExistsByEmailAndNotPublicId(patientRequestDTO.getEmail(), UUID.fromString(id))) {
            throw new EmailAlreadyExistsException(PATIENT_WITH_EMAIL_EXISTS);
        }
        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);
        return convertToDTO(updatedPatient);
    }

    @Transactional
    @Override
    public void deletePatient(String id) {
        Patient patient = getPatientById(id);
        patientRepository.delete(patient);
    }

    @Override
    public PatientResponseDTO convertToDTO(Patient patient) {
        PatientResponseDTO dto = new PatientResponseDTO();
        dto.setId(patient.getPublicId().toString());
        dto.setName(patient.getName());
        dto.setAddress(patient.getAddress());
        dto.setEmail(patient.getEmail());
        dto.setDateOfBirth(patient.getDateOfBirth().toString());
        return dto;
    }

    @Override
    public List<PatientResponseDTO> convertToDTOList(List<Patient> patientList) {
        if(CollectionUtils.isEmpty(patientList)){
            return List.of();
        }
        List<PatientResponseDTO> result = new ArrayList<>();
        for(Patient list : patientList){
            result.add(convertToDTO(list));
        }
        return result;
    }

    @Override
    public Patient convertToEntity(PatientRequestDTO patientRequestDTO) {
        Patient patient = new Patient();
        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        patient.setRegisteredDate(LocalDate.parse(patientRequestDTO.getRegisteredDate()));
        return patient;
    }
}
