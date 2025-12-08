package com.pm.analyticsservice.repository;

import com.pm.analyticsservice.model.PaymentEventModel;
import com.pm.analyticsservice.model.dto.DailyPaymentSummary;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentEventRepository extends CrudRepository<PaymentEventModel, UUID> {

    @Query("SELECT event_date, state, sum(total_payments) as total_payments, sum(total_amount) as total_amount FROM analyticsservice.payment_events_daily_summary GROUP BY event_date, state ORDER BY event_date DESC")
    List<DailyPaymentSummary> getDailyPaymentSummary();
}
