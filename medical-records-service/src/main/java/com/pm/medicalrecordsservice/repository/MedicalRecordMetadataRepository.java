package com.pm.medicalrecordsservice.repository;

import com.pm.medicalrecordsservice.model.MedicalRecordMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalRecordMetadataRepository extends MongoRepository<MedicalRecordMetadata, String> {

    /**
     * Finds all medical record metadata for a given patient.
     *
     * @param patientId The ID of the patient.
     * @return A list of medical record metadata.
     */
    List<MedicalRecordMetadata> findByPatientId(String patientId);
}
