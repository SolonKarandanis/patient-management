package com.pm.paymentservice.repository;

import com.pm.paymentservice.model.PaymentEntity;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepository extends AbstractRepository<PaymentEntity,Integer> {
}
