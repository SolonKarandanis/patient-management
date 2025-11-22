package com.pm.authservice.i18n.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTranslationDTO {

    @NotNull(message = "{error.translation.resource-id.required}")
    private Integer resourceId;

    private String textValue;

    @NotNull(message = "{error.translation.language-id.required}")
    private Integer languageId;
}
