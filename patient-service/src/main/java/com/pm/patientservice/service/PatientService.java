package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.model.Patient;

import java.util.List;
import java.util.UUID;

public interface PatientService {
    public List<PatientResponseDTO> getPatients();
    public Patient getPatientById(String id) throws PatientNotFoundException;
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO)
            throws EmailAlreadyExistsException;
    public PatientResponseDTO updatePatient(String id, PatientRequestDTO patientRequestDTO)
            throws EmailAlreadyExistsException, PatientNotFoundException;
    public void deletePatient(String id);
    public PatientResponseDTO convertToDTO(Patient patient);
    public List<PatientResponseDTO> convertToDTOList(List<Patient> patientList);
    public Patient convertToEntity(PatientRequestDTO patientRequestDTO);
}
