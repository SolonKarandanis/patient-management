package com.pm.analyticsservice.repository;

import com.pm.analyticsservice.model.PatientEventModel;
import com.pm.analyticsservice.model.dto.DailyEventCount;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PatientEventRepository extends CrudRepository<PatientEventModel, UUID> {

    @Query("SELECT event_date, event_type, sum(total_events) as total_events FROM analyticsservice.patient_events_daily_summary GROUP BY event_date, event_type ORDER BY event_date DESC")
    List<DailyEventCount> getDailyPatientSummary();
}
