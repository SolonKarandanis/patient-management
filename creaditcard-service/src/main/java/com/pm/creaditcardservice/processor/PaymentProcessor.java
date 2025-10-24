package com.pm.creaditcardservice.processor;

import java.math.BigDecimal;

public interface PaymentProcessor {

    void makePayment(BigDecimal amount);
}
