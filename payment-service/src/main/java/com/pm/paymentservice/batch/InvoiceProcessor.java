package com.pm.paymentservice.batch;


import com.pm.paymentservice.model.Invoice;
import com.pm.paymentservice.model.InvoiceStatus;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class InvoiceProcessor implements ItemProcessor<Long, Invoice> {

    @Override
    public Invoice process(Long patientId) throws Exception {
        // In a real implementation, this would:
        // 1. Call the patient-service to get the patient's public ID.
        // 2. Call the billing-service to get all billable items for the last month.
        // 3. Calculate the total amount from the billing items.

        // Placeholder data for now
        UUID patientPublicId = UUID.randomUUID(); // Placeholder
        BigDecimal totalAmount = new BigDecimal("100.00"); // Placeholder

        LocalDate today = LocalDate.now();

        return Invoice.builder()
                .publicId(UUID.randomUUID())
                .patientId(patientId)
                .patientPublicId(patientPublicId)
                .totalAmount(totalAmount)
                .createdDate(today)
                .dueDate(today.plusDays(30))
                .status(InvoiceStatus.ISSUED)
                .build();
    }
}
