package com.pm.paymentservice.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentRequestDTO {
    private BigDecimal amount;
    private PaymentMethod method;
}
