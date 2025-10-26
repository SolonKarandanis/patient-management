package com.pm.paymentservice.repository;

import com.pm.paymentservice.domain.PaymentEntity;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepository extends AbstractRepository<PaymentEntity,Integer> {
}
