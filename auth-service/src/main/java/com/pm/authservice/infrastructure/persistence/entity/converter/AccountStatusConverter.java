package com.pm.authservice.infrastructure.persistence.entity.converter;

import com.pm.authservice.domain.model.AccountStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AccountStatusConverter implements AttributeConverter<AccountStatus, String> {

    @Override
    public String convertToDatabaseColumn(AccountStatus state) {
        if (state == null) {
            return null;
        }
        return state.getValue();
    }

    @Override
    public AccountStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return AccountStatus.fromValue(dbData);
    }
}
