package com.pm.paymentservice.service;

import com.pm.paymentservice.domain.PaymentEntity;
import com.pm.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class PaymentServiceBeanTest {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    PaymentEntity payment;

    @BeforeEach
    void setUp() {
        payment = PaymentEntity.builder().amount(new BigDecimal("12.99")).build();
    }

    @Test
    void preAuthorize() {
        PaymentEntity savedPayment = paymentService.newPayment(payment);
        paymentService.preAuthorize(savedPayment.getId());
        PaymentEntity preAuthedPayment =paymentRepository.findByIdOpt(savedPayment.getId()).get();
    }
}