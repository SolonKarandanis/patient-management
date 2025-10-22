package com.pm.patientservice.repository;

import com.pm.patientservice.model.PatientEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientEventRepository extends JpaRepository<PatientEventEntity,Integer> {
}
