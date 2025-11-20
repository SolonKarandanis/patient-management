package com.pm.authservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class I18nResourceManagementRequestDTO extends SearchRequestDTO{

    private String languageId;
    private String moduleId;
    private String term;
}
