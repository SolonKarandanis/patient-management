package com.pm.patientservice.repository;

import com.pm.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient,Integer> {
    boolean existsByEmail(String email);

    @Query(name = Patient.EXISTS_BY_EMAIL_AND_NOT_PUBLIC_ID)
    boolean patientExistsByEmailAndNotPublicId(String email, UUID publicId);

    @Query(name = Patient.FIND_BY_PUBLIC_ID)
    Optional<Patient> findOneByPublicId(@Param("publicId")UUID publicId);
}
