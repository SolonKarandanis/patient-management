package com.pm.analyticsservice.repository;

import com.pm.analyticsservice.model.PatientEventModel;
import com.pm.analyticsservice.model.dto.DailyEventCount;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PatientEventRepository extends CrudRepository<PatientEventModel, UUID> {

    @Modifying
    @Query("INSERT INTO patient_events (id, patientId, name, email, event_type, event_timestamp) VALUES (:#{#patientEvent.id}, :#{#patientEvent.patientId}, :#{#patientEvent.name}, :#{#patientEvent.email}, :#{#patientEvent.event_type}, :#{#patientEvent.event_timestamp})")
    void insert(@Param("patientEvent") PatientEventModel patientEvent);

    @Query("SELECT event_date, event_type, sum(total_events) as total_events FROM analyticsservice.patient_events_daily_summary GROUP BY event_date, event_type ORDER BY event_date DESC")
    List<DailyEventCount> getDailyPatientSummary();
}
