/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pm.authservice.user.model.converter;


import com.pm.authservice.user.model.AccountStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 *
 * @author solon
 */
@Converter(autoApply = true)
public class AccountStatusConverter implements AttributeConverter<AccountStatus, String>{

    @Override
    public String convertToDatabaseColumn(AccountStatus state) {
        return switch (state) {
            case ACTIVE -> "account.active";
            case INACTIVE -> "account.inactive";
            case DELETED -> "account.deleted";
            default -> throw new IllegalArgumentException("AccountState [" + state + "] not supported");
        };
    }

    @Override
    public AccountStatus convertToEntityAttribute(String dbData) {
        return switch (dbData) {
            case "account.deleted" -> AccountStatus.DELETED;
            case "account.active" -> AccountStatus.ACTIVE;
            case "account.inactive" -> AccountStatus.INACTIVE;
            default -> throw new IllegalArgumentException("AccountState [" + dbData + "] not supported");
        };
    }
    
}
