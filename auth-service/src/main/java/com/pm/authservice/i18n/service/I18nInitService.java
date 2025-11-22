package com.pm.authservice.i18n.service;

import com.pm.authservice.i18n.dto.I18nTranslationImportDTO;
import com.pm.authservice.i18n.model.I18nModule;
import com.pm.authservice.i18n.model.Language;

import java.util.Map;

public interface I18nInitService {

    void initI18nTranslations();

    void importI18nTranslationsOfModule(I18nModule i18nModule, Map<Language, I18nTranslationImportDTO> langImportDto);
}
