package com.pm.authservice.i18n.service;

import com.pm.authservice.i18n.dto.UpdateTranslationDTO;
import com.pm.authservice.i18n.event.TranslationsUpdatedEvent;
import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.i18n.model.I18nLabel;
import com.pm.authservice.i18n.model.I18nModule;
import com.pm.authservice.i18n.model.I18nTranslation;
import com.pm.authservice.i18n.model.Language;
import com.pm.authservice.i18n.repository.I18nLabelRepository;
import com.pm.authservice.i18n.repository.I18nModuleRepository;
import com.pm.authservice.i18n.repository.I18nTranslationRepository;
import com.pm.authservice.i18n.repository.LanguageRepository;
import com.pm.authservice.util.AppResourceUtil;
import com.pm.authservice.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service("i18nService")
@Transactional(readOnly = true)
public class I18nServiceBean implements I18nService{

    private final LanguageRepository languageRepository;
    private final I18nModuleRepository i18nModuleRepository;
    private final I18nLabelRepository i18nLabelRepository;
    private final I18nTranslationRepository i18nTranslationRepository;
    private final ResourceLoader resourceLoader;
    private final ApplicationEventPublisher publisher;
    private final I18nService selfService;

    public I18nServiceBean(
            LanguageRepository languageRepository,
            I18nModuleRepository i18nModuleRepository,
            I18nLabelRepository i18nLabelRepository,
            I18nTranslationRepository i18nTranslationRepository,
            ResourceLoader resourceLoader,
            ApplicationEventPublisher publisher,
            @Lazy I18nService selfService
    ) {
        this.languageRepository = languageRepository;
        this.i18nModuleRepository = i18nModuleRepository;
        this.i18nLabelRepository = i18nLabelRepository;
        this.i18nTranslationRepository = i18nTranslationRepository;
        this.resourceLoader = resourceLoader;
        this.publisher = publisher;
        this.selfService = selfService;
    }

    @Override
    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<String> getModuleNames() {
        return i18nModuleRepository.getI18nModules().stream()
                .map(I18nModule::getModuleName)
                .toList();
    }

    @Override
    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<String> getActiveModuleNames() {
        return getActiveModules().stream()
                .map(I18nModule::getModuleName)
                .toList();
    }

    @Override
    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<I18nModule> getActiveModules() {
        return i18nModuleRepository.getActiveI18nModules();
    }

    @Override
    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<String> getLanguageIsoCodes() {
        return getLanguages().stream()
                .map(Language::getIsoCode)
                .toList();
    }

    @Override
    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<Language> getLanguages() {
        return languageRepository.getLanguages();
    }

    @Override
    @Cacheable( cacheNames = "resources",key="#moduleName.concat('-').concat(#langIso)")
    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public Map<String, String> getTranslationsByModuleAndLangIsoCode(String moduleName, String langIso) {
        log.info(" ------>I18nServiceBean------> getTranslationsByModuleAndLangIsoCode[moduleName: {}, langIso: {}] ", moduleName, langIso);
        if (moduleName != null && langIso != null) {
            return i18nTranslationRepository.getTranslationsByModuleAndLangIsoCode(moduleName, langIso);
        } else {
            return Map.of();
        }
    }

    @Override
    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public Map<String, String> getResourcePropertiesByModuleAndLangIsoCode(String moduleName, String langIso) throws BusinessException {
        Map<String, String> outputMap = new LinkedHashMap<>();

        // Classpath resource: <module_name>_<lang>.properties
        String appResourceName = moduleName + "_" + langIso + ".properties";
        Properties appResourceProps = AppResourceUtil.loadResourceAsProperties(resourceLoader, appResourceName, StandardCharsets.UTF_8);

        appResourceProps.forEach((propKey, propValue) -> {
            outputMap.put((String) propKey, (String) propValue);
        });
        return outputMap;
    }

    @Override
    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<I18nTranslation> getTranslationRecordsByModuleIdAndLangIdAndNoUserModified(Integer moduleId, Integer langId) {
        return i18nTranslationRepository.getTranslationRecordsByModuleIdAndLangIdAndNoUserModified(moduleId, langId);
    }

    @Override
    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<String> getLabelKeysHavingUserModifiedTranslationByModuleIdAndLangId(Integer moduleId, Integer langId) {
        return i18nTranslationRepository.getLabelKeysHavingUserModifiedTranslationByModuleIdAndLangId(moduleId, langId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int importI18nTranslations(Integer moduleId, List<I18nTranslation> insertionsList, List<I18nTranslation> updatesList, List<Integer> deletionsList,
                                      boolean isInsertLabels, boolean isDeleteOrphanLabels) {
        // Step-1: Inserts
        List<I18nLabel> i18nLabelsInserted = new ArrayList<>();
        if (!insertionsList.isEmpty()) {
            if (isInsertLabels) {
                List<String> labelResKeys = insertionsList.stream().map(I18nTranslation::getI18nLabelResourceKey).collect(Collectors.toList());
                i18nLabelsInserted.addAll(importI18nLabels(moduleId, labelResKeys));
            }
            boolean isGetLabels = insertionsList.stream().anyMatch(i18nTrn -> i18nTrn.getI18nLabelId() == null);
            List<I18nLabel> i18nLabels = isGetLabels ? i18nLabelRepository.getI18nLabelsByModuleId(moduleId) : new ArrayList<>();
            Map<String, I18nLabel> i18nLabelsMap = i18nLabels.stream().collect(Collectors.toMap(I18nLabel::getResourceKey, Function.identity()));
            i18nLabelsInserted.forEach(i18nLabel -> i18nLabelsMap.putIfAbsent(i18nLabel.getResourceKey(), i18nLabel));
            // Step-1.2: Insert translations
            insertionsList.forEach(i18nTranslation -> {
                if (i18nTranslation.getI18nLabelId() == null) {
                    I18nLabel associatedLabel = i18nLabelsMap.get(i18nTranslation.getI18nLabelResourceKey());
                    if (associatedLabel == null) {
                        log.warn(" Warning: ModuleId={}, ResourceKey={}, I18nLabel NULL ", moduleId, i18nTranslation.getI18nLabelResourceKey());
                    } else {
                        i18nTranslation.setI18nLabelId(associatedLabel.getId());
                    }
                }
            });
            i18nTranslationRepository.saveAllAndFlush(insertionsList);
        }
        // Step-2: Updates
        if (!updatesList.isEmpty()) {
            i18nTranslationRepository.saveAllAndFlush(updatesList);
        }
        // Step-3: Deletions
        List<List<Integer>> deletionsLists = CollectionUtil.splitList(deletionsList, 100);
        deletionsLists.forEach(i18nTranslationRepository::deleteI18nTranslationByIds);
        // Step-4: Delete labels with no translations
        int iLabelsDeleted = isDeleteOrphanLabels ? deleteI18nLabelsWithNoTranslationsByModuleId(moduleId) : 0;
        log.info(" END: I18nLabels-Inserted={}, I18nTranslations-Inserted={}, I18nTranslations-updated={}, I18nTranslations-deleted={}, I18nLabels-deleted={} ",
                i18nLabelsInserted.size(), insertionsList.size(), updatesList.size(), deletionsList.size(), iLabelsDeleted);
        return (i18nLabelsInserted.size() + insertionsList.size() + updatesList.size() + deletionsList.size() + iLabelsDeleted);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean lockUpdateModuleById(Integer moduleId, String updateId, Date updStartTime) {
        return i18nModuleRepository.lockUpdateModuleById(moduleId, updateId, updStartTime) > 0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean unlockUpdateModuleById(Integer moduleId, String updateId, Date updEndTime) {
        return i18nModuleRepository.unlockUpdateModuleById(moduleId, updateId, updEndTime) > 0;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void editLabelsAndSendNotification(List<UpdateTranslationDTO> updateRequest) {
        List<Integer> resourceIds = updateRequest.stream()
                .map(UpdateTranslationDTO::getResourceId)
                .toList();
        List<I18nTranslation> translations = i18nTranslationRepository.getTranslationsByResourceIds(resourceIds);
        selfService.editLabels(updateRequest,translations);
        Map<String, List<String>> modulesLanguagesMap = translations.stream()
                .collect(Collectors.groupingBy(tran->tran.getI18nLabel().getI18nModule().getModuleName(),
                        Collectors.mapping(tran->tran.getLanguage().getIsoCode(), Collectors.toList())));

        modulesLanguagesMap.forEach((key, value) -> value.stream()
                .distinct()
                .forEach(isoCode -> selfService.clearCacheByModuleAndIso(key, isoCode)));
        publisher.publishEvent(new TranslationsUpdatedEvent());
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void editLabels(List<UpdateTranslationDTO> updateRequest, List<I18nTranslation> translations) {
        for(UpdateTranslationDTO  dto : updateRequest) {
            translations.stream().filter(filterByLanguageAndResource(dto.getLanguageId(),dto.getResourceId()))
                    .findFirst()
                    .ifPresent(tr -> {
                        tr.setTextValue(dto.getTextValue());
                        tr.setIsUserModified(true);
                    });
        }
        i18nTranslationRepository.saveAll(translations);
    }

    @CacheEvict( cacheNames = "resources",key="#moduleName.concat('-').concat(#langIso)")
    @Override
    public void clearCacheByModuleAndIso(String moduleName, String langIso) {
        log.info("-----> I18nServiceBean -----> clearCacheByModuleAndIso (moduleName:{}, langIso:{}) cleared", moduleName, langIso);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<I18nLabel> importI18nLabels(Integer moduleId, List<String> labelResKeys) {
        List<I18nLabel> i18nLabels = i18nLabelRepository.getI18nLabelsByModuleId(moduleId);
        Map<String, I18nLabel> i18nLabelsDbMap = i18nLabels.stream().collect(Collectors.toMap(I18nLabel::getResourceKey, Function.identity()));
        List<I18nLabel> i18nLabelsToInsert = labelResKeys.stream().filter(resourceKey -> !i18nLabelsDbMap.containsKey(resourceKey)).map(resourceKey -> {
            I18nLabel i18nLabel = new I18nLabel();
            i18nLabel.setResourceKey(resourceKey);
            i18nLabel.setI18nModuleId(moduleId);
            return i18nLabel;
        }).toList();

        return new ArrayList<>(i18nLabelRepository.saveAllAndFlush(i18nLabelsToInsert));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteI18nLabelsWithNoTranslationsByModuleId(Integer moduleId) {
        int iLabelsDeleted = 0;
        if (i18nLabelRepository.getCountOfI18nLabelsWithNoTranslationsByModuleId(moduleId) > 0) {
            iLabelsDeleted = i18nLabelRepository.deleteI18nLabelsWithNoTranslationsByModuleId(moduleId);
        }
        return iLabelsDeleted;
    }

    private Predicate<I18nTranslation> filterByLanguageAndResource(final Integer languageId, final Integer resourceId) {
        return tr-> tr.getLanguageId().equals(languageId) && tr.getI18nLabelId().equals(resourceId);
    }
}
