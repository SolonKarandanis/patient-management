package com.pm.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStats {
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal avgAmount;
}
