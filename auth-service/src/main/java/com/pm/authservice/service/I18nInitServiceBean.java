package com.pm.authservice.service;


import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.model.I18nModule;
import com.pm.authservice.model.I18nTranslation;
import com.pm.authservice.model.Language;
import com.pm.authservice.util.AppResourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service("i18nInitService")
@Transactional
public class I18nInitServiceBean implements I18nInitService {

    private static final String RESOURCE_NAME_PATTERN = "{MODULE_NAME}_{LANG_ISO}.properties";

    @Value("${i18n.resources.DB.enabled:false}")
    private Boolean i18nDbEnabled;

    private final I18nService i18nService;
    private final ApplicationContext applicationContext;

    public I18nInitServiceBean(
            I18nService i18nService,
            ApplicationContext applicationContext) {
        this.i18nService = i18nService;
        this.applicationContext = applicationContext;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void initI18nTranslations() {
        if (Boolean.TRUE.equals(i18nDbEnabled)) {
            onInitI18nTranslations();
        }
    }

    private void onInitI18nTranslations(){
        List<Language> languages = i18nService.getLanguages();
        List<String> langIsoCodes = languages.stream().map(Language::getIsoCode).collect(Collectors.toList());

        List<I18nModule> i18nModules = i18nService.getActiveModules();
        List<String> i18nModuleNames = i18nModules.stream().map(I18nModule::getModuleName).collect(Collectors.toList());

        Date updDateTime = Calendar.getInstance().getTime();
        String updProcessId = UUID.randomUUID().toString();
        log.info(" INIT-I18nTranslations[langIsoCodes: {}, modules: {}, UpdateUUID: {}] ", langIsoCodes, i18nModuleNames, updProcessId);

        ExecutorService execService = Executors.newVirtualThreadPerTaskExecutor();
        Map<Integer, Future<?>> modOutputMap = new LinkedHashMap<>();
        long startTime = System.currentTimeMillis();

        try (execService) {
            i18nModules.forEach(i18nModule -> {
                Future<?> modOutput = execService.submit(() -> {
                    if (i18nService.lockUpdateModuleById(i18nModule.getId(), updProcessId, updDateTime)) {
                        languages.forEach(language -> {
                            try {
                                initI18nTranslationsByModuleAndLanguage(language, i18nModule);
                            } catch (BusinessException e) {
                                log.error("INIT-I18nTranslations: ERROR: {}",e.getMessage());
                            }
                        });
                        // Update complete, release module lock
                        if (!i18nService.unlockUpdateModuleById(i18nModule.getId(), updProcessId, Calendar.getInstance().getTime())) {
                            log.warn(" INIT-I18nTranslations: Unlock module {}-{} did not succeed ", i18nModule.getId(), i18nModule.getModuleName());
                        }
                    } else {
                        log.info(" INIT-I18nTranslations: Could not lock module {}-{}, skipping update ", i18nModule.getId(), i18nModule.getModuleName());
                    }
                });
                modOutputMap.put(i18nModule.getId(), modOutput);
            });
            // Wait for all executions to complete
            AtomicBoolean atLeastOneFailed = new AtomicBoolean(false);
            modOutputMap.forEach((moduleId, moduleOutput) -> {
                try {
                    moduleOutput.get();
                } catch (InterruptedException | ExecutionException e) {
                    log.error(" ERROR-INIT-I18nTranslations: Module {} import process failed: ", moduleId, e);
                    atLeastOneFailed.set(true);
                }
            });
            if (atLeastOneFailed.get()) {
                throw new IllegalStateException(" ERROR-INIT-I18nTranslations: Import failed ");
            }
        }
        finally {
            long timeTaken = System.currentTimeMillis() - startTime;
            log.info(" INIT-I18nTranslations: TIME-TAKEN = {} miliseconds ", timeTaken);
        }
    }

    private void initI18nTranslationsByModuleAndLanguage(final Language language, final I18nModule module)
            throws BusinessException {
        String appResourceName = getAppResourceName(module.getModuleName(), language.getIsoCode());
        Properties propsResource = AppResourceUtil.loadResourceAsProperties(applicationContext, appResourceName, StandardCharsets.UTF_8);
        log.info(" CHECKING-1: AppResource = {}, exists? {} ", appResourceName, (!propsResource.isEmpty()));
        List<I18nTranslation> i18nTranslations = i18nService.getTranslationRecordsByModuleIdAndLangIdAndNoUserModified(module.getId(), language.getId());
        List<String> labelKeysUserUpdAsList = i18nService.getLabelKeysHavingUserModifiedTranslationByModuleIdAndLangId(module.getId(), language.getId());
        log.info(" CHECKING-2: AppResource = {}, Total properties = {}, Messages/DB = {}, Labels UserModified = {} ", appResourceName, propsResource.size(),
                i18nTranslations.size(), labelKeysUserUpdAsList.size());
        Map<String, I18nTranslation> i18nTranslationsMap = i18nTranslations.stream()
                .collect(Collectors.toMap(i18nTranslation -> i18nTranslation.getI18nLabel().getResourceKey(), Function.identity(), (i1, i2) -> {
                    log.warn(" WARNING: I18nModule={}, Language={}, I18nTranslations ids {} and {} are both linked with I18nLabel {}. ", module.getModuleName(),
                            language.getIsoCode(), i1.getId(), i2.getId(), i1.getI18nLabel().getResourceKey());
                    return i2;
                }, LinkedHashMap::new));

        Set<String> labelKeysUpd = new LinkedHashSet<>(labelKeysUserUpdAsList);
        initI18nTranslationsByModuleAndLanguage(language, module, propsResource, i18nTranslationsMap, labelKeysUpd);
    }

    private void initI18nTranslationsByModuleAndLanguage(final Language language, final I18nModule module, final Properties propsResource,
                                                         final Map<String, I18nTranslation> i18nTranslationsMap, final Set<String> labelKeysUpd){
        Map<String, I18nTranslation> insertionsMap = new LinkedHashMap<>();
        List<I18nTranslation> updatesList = new ArrayList<>();
        List<Integer> deletionsList = new ArrayList<>();
        // Step-1: Check which properties do not exist in DB (Inserts/Updates).
        propsResource.forEach((obPropKey, obPropVal) -> {
            String propKey = (String) obPropKey;
            String propVal = (String) obPropVal;
            if (!labelKeysUpd.contains(propKey)) {
                // Skipping labels that have been explicitly modified in DB.
                if (i18nTranslationsMap.containsKey(propKey)) {
                    I18nTranslation i18nTranslation = i18nTranslationsMap.get(propKey);
                    if (!(i18nTranslation.getTextValue().equals(propVal))) {
                        i18nTranslation.setTextValue(propVal);
                        updatesList.add(i18nTranslation);
                    }
                } else {
                    I18nTranslation i18nTranslation = new I18nTranslation();
                    i18nTranslation.setIsUserModified(Boolean.FALSE);
                    i18nTranslation.setLanguageId(language.getId());
                    i18nTranslation.setTextValue(propVal);
                    insertionsMap.put(propKey, i18nTranslation);
                }
            }
        });
        // Step-2: Check which DB Translations do not exist in properties (Delete).
        i18nTranslationsMap.forEach((i18nResourceKey, i18nTranslation) -> {
            if (propsResource.getProperty(i18nResourceKey) == null) {
                deletionsList.add(i18nTranslation.getId());
            }
        });
        log.info(" STEP: Inserts = {}, Updates = {}, Deletions = {} ", insertionsMap.size(), updatesList.size(), deletionsList.size());
        i18nService.importI18nTranslations(module.getId(), insertionsMap, updatesList, deletionsList);
    }

    private String getAppResourceName(final String moduleName, final String langIso) {
        return RESOURCE_NAME_PATTERN.replace("{MODULE_NAME}", moduleName).replace("{LANG_ISO}", langIso);
    }
}
