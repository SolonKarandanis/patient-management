package com.pm.authservice.infrastructure.application;

import com.pm.authservice.infrastructure.web.dto.I18nTranslationImportDTO;
import com.pm.authservice.infrastructure.persistence.entity.I18nModule;
import com.pm.authservice.infrastructure.persistence.entity.Language;

import java.util.Map;

public interface I18nInitService {

    void initI18nTranslations();

    void importI18nTranslationsOfModule(I18nModule i18nModule, Map<Language, I18nTranslationImportDTO> langImportDto);
}
