package com.pm.paymentservice.batch;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

@Component
public class PatientIdReader implements ItemReader<Long> {
    // In a real implementation, this would call the billing-service
    // to get a list of patient IDs with unprocessed charges.
    private final Long[] patientIds = {1L, 2L, 3L, 4L, 5L}; // Placeholder data
    private int index = 0;

    @Override
    public Long read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (index < patientIds.length) {
            return patientIds[index++];
        } else {
            return null; // Signals the end of the data
        }
    }
}
