package com.pm.authservice.i18n.dto;

import com.pm.authservice.dto.SearchRequestDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class I18nResourceManagementRequestDTO extends SearchRequestDTO {

    private String languageId;
    private String moduleId;
    private String term;
}
