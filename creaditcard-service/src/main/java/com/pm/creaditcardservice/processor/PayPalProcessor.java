package com.pm.creaditcardservice.processor;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PayPalProcessor implements PaymentProcessor {

    @Override
    public void makePayment(BigDecimal amount) {

    }
}
