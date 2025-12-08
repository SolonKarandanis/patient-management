package com.pm.analyticsservice.repository;

import com.pm.analyticsservice.model.PatientEventModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PatientEventRepository extends CrudRepository<PatientEventModel, UUID> {
}
