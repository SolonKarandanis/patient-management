package com.pm.analyticsservice.repository;

import com.pm.analyticsservice.domain.PaymentEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentEventRepository extends CrudRepository<PaymentEvent, UUID> {
}
