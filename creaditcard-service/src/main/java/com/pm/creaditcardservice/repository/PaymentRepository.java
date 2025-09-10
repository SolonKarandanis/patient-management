package com.pm.creaditcardservice.repository;

import com.pm.creaditcardservice.domain.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity,Integer> {
}
