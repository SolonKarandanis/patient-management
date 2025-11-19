package com.pm.authservice.service;

import com.pm.authservice.dto.I18nTranslationImportDTO;
import com.pm.authservice.model.I18nModule;
import com.pm.authservice.model.Language;

import java.util.Map;

public interface I18nInitService {

    void initI18nTranslations();

    void importI18nTranslationsOfModule(I18nModule i18nModule, Map<Language, I18nTranslationImportDTO> langImportDto);
}
