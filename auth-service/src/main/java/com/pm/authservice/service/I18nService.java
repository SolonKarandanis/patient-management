package com.pm.authservice.service;

import com.pm.authservice.dto.UpdateTranslationDTO;
import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.model.I18nModule;
import com.pm.authservice.model.I18nTranslation;
import com.pm.authservice.model.Language;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface I18nService {

    List<String> getModuleNames();

    List<String> getActiveModuleNames();

    List<I18nModule> getActiveModules();

    List<String> getLanguageIsoCodes();

    List<Language> getLanguages();

    Map<String, String> getTranslationsByModuleAndLangIsoCode(String moduleName, String langIso);

    Map<String, String> getResourcePropertiesByModuleAndLangIsoCode(String moduleName, String langIso) throws BusinessException;

    List<I18nTranslation> getTranslationRecordsByModuleIdAndLangIdAndNoUserModified(Integer moduleId, Integer langId);

    List<String> getLabelKeysHavingUserModifiedTranslationByModuleIdAndLangId(Integer moduleId, Integer langId);

    int importI18nTranslations(Integer moduleId, Map<String, I18nTranslation> insertionsMap, List<I18nTranslation> updatesList, List<Integer> deletionsList);

    boolean lockUpdateModuleById(Integer moduleId, String updateId, Date updStartTime);

    boolean unlockUpdateModuleById(Integer moduleId, String updateId, Date updEndTime);

    void editLabelsAndSendNotification(List<UpdateTranslationDTO> updateRequest);

    void editLabels(List<UpdateTranslationDTO> updateRequest,List<I18nTranslation> translations);

    void clearCacheByModuleAndIso(String moduleName, String langIso);
}
