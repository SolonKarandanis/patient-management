package com.pm.notificationservice.email.model.converter;

import com.pm.notificationservice.email.model.EmailStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EmailStatusConverter implements AttributeConverter<EmailStatus, String> {

    @Override
    public String convertToDatabaseColumn(EmailStatus state) {
        return switch (state) {
            case PENDING -> "PENDING";
            case FAILED -> "FAILED";
            case SENT -> "SENT";
            default -> throw new IllegalArgumentException("EmailStatus [" + state + "] not supported");
        };
    }

    @Override
    public EmailStatus convertToEntityAttribute(String dbData) {
        return switch (dbData) {
            case "PENDING" -> EmailStatus.PENDING;
            case "FAILED" -> EmailStatus.FAILED;
            case "SENT" -> EmailStatus.SENT;
            default -> throw new IllegalArgumentException("EmailStatus [" + dbData + "] not supported");
        };
    }
}
