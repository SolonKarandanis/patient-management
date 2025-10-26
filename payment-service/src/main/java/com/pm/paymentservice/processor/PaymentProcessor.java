package com.pm.paymentservice.processor;

import java.math.BigDecimal;

public interface PaymentProcessor {

    void makePayment(BigDecimal amount);
}
