package com.pm.authservice.service;


import com.pm.authservice.dto.I18nTranslationImportDTO;
import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.model.I18nLabel;
import com.pm.authservice.model.I18nModule;
import com.pm.authservice.model.I18nTranslation;
import com.pm.authservice.model.Language;
import com.pm.authservice.util.AppResourceUtil;
import com.pm.authservice.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service("i18nInitService")
@Transactional
public class I18nInitServiceBean implements I18nInitService {

    private static final String RESOURCE_NAME_PATTERN = "{MODULE_NAME}_{LANG_ISO}.properties";
    private static final int LABELS_PER_THREAD = 100;
    private static final int TRANSLATIONS_PER_THREAD = 100;

    @Value("${i18n.resources.DB.enabled:false}")
    private Boolean i18nDbEnabled;

    private final I18nService i18nService;
    private final I18nInitService selfService;
    private final ApplicationContext applicationContext;

    public I18nInitServiceBean(
            @Lazy I18nService i18nService,
            @Lazy I18nInitService selfService,
            ApplicationContext applicationContext) {
        this.i18nService = i18nService;
        this.selfService = selfService;
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
        Map<I18nModule, Future<?>> modOutputMap = new LinkedHashMap<>();
        long startTime = System.currentTimeMillis();
        try (execService) {
            i18nModules.forEach(i18nModule -> {
                Future<?> modOutput = execService.submit(() -> {
                    if (i18nService.lockUpdateModuleById(i18nModule.getId(), updProcessId, updDateTime)) {
                        // Step-1: Get module imports for all languages
                        Map<Language, I18nTranslationImportDTO> langImportDto = new LinkedHashMap<>();
                        languages.forEach(language -> {
                            try {
                                langImportDto.put(language, getI18nTranslationsByModuleAndLanguageImportDto(language, i18nModule));
                            } catch (BusinessException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        // Step-2: Import translations
                        selfService.importI18nTranslationsOfModule(i18nModule, langImportDto);
                        // Step-3: Update complete, release module lock
                        if (!i18nService.unlockUpdateModuleById(i18nModule.getId(), updProcessId, Calendar.getInstance().getTime())) {
                            log.warn(" INIT-I18nTranslations: Unlock module {}-{} did not succeed ", i18nModule.getId(), i18nModule.getModuleName());
                        }
                    } else {
                        log.info(" INIT-I18nTranslations: Could not lock module {}-{}, skipping update ", i18nModule.getId(), i18nModule.getModuleName());
                    }
                });
                modOutputMap.put(i18nModule, modOutput);
            });
            // Wait for all executions to complete
            awaitCompletion(modOutputMap);
        } finally {
            long timeTaken = System.currentTimeMillis() - startTime;
            log.info(" INIT-I18nTranslations: TIME-TAKEN = {} miliseconds ", timeTaken);
        }
    }

    private void awaitCompletion(final Map<I18nModule, Future<?>> modOutputMap) {
        Exception exception = null;
        I18nModule moduleFailed = null;
        for (Map.Entry<I18nModule, Future<?>> modEntry : modOutputMap.entrySet()) {
            try {
                modEntry.getValue().get();
            } catch (InterruptedException | ExecutionException e) {
                exception = e;
                moduleFailed = modEntry.getKey();
                break;
            }
        }
        if (exception != null) {
            throw new IllegalStateException(" ERROR-INIT-I18nTranslations: Module "
                    + Optional.ofNullable(moduleFailed).map(I18nModule::getModuleName).orElse("") + " import process failed ", exception);
        }
    }

    private I18nTranslationImportDTO getI18nTranslationsByModuleAndLanguageImportDto(final Language language, final I18nModule module) throws BusinessException {
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

        return getI18nTranslationsByModuleAndLanguageImportDto(language, module, propsResource, i18nTranslationsMap, labelKeysUpd);
    }

    private I18nTranslationImportDTO getI18nTranslationsByModuleAndLanguageImportDto(final Language language, final I18nModule module,
                                                                                     final Properties propsResource,
                                                                                     final Map<String, I18nTranslation> i18nTranslationsMap, final Set<String> labelKeysUpd) {
        I18nTranslationImportDTO outputAsDto = new I18nTranslationImportDTO();
        List<I18nTranslation> insertionsList = new ArrayList<>();
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
                    i18nTranslation.setI18nLabelResourceKey(propKey);
                    i18nTranslation.setLanguageId(language.getId());
                    i18nTranslation.setIsUserModified(Boolean.FALSE);
                    i18nTranslation.setTextValue(propVal);
                    insertionsList.add(i18nTranslation);
                }
            }
        });
        // Step-2: Check which DB Translations do not exist in properties (Delete).
        i18nTranslationsMap.forEach((i18nResourceKey, i18nTranslation) -> {
            if (propsResource.getProperty(i18nResourceKey) == null) {
                deletionsList.add(i18nTranslation.getId());
            }
        });
        log.info(" INIT-I18nTranslations: Module {}-{}, Language {}-{} TOTAL: [Inserts = {}, Updates = {}, Deletions = {}] ", module.getId(),
                module.getModuleName(), language.getId(), language.getIsoCode(), insertionsList.size(), updatesList.size(), deletionsList.size());
        outputAsDto.setInsertionsList(insertionsList);
        outputAsDto.setUpdatesList(updatesList);
        outputAsDto.setDeletionsList(deletionsList);
        return outputAsDto;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void importI18nTranslationsOfModule(I18nModule i18nModule, Map<Language, I18nTranslationImportDTO> langImportDto) {
        // Step-1: Insert missing labels
        Set<String> labelResourceKeys = langImportDto.values().stream().map(I18nTranslationImportDTO::getInsertLabelKeys).flatMap(Set::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        List<I18nLabel> i18nLabelsInsertedAsList = new ArrayList<>();
        if (!labelResourceKeys.isEmpty()) {
            List<String> labelResKeysAsList = new ArrayList<>(labelResourceKeys);
            // Assign insert of labels to a set of virtual threads, each inserting maximum LABELS_PER_THREAD entries
            List<List<String>> labelResKeysTotal = CollectionUtil.splitList(labelResKeysAsList, LABELS_PER_THREAD);
            ExecutorService labelInsertService = Executors.newVirtualThreadPerTaskExecutor();
            List<Future<List<I18nLabel>>> labelInsertOutputAsList = new ArrayList<>();
            try (labelInsertService) {
                labelResKeysTotal.forEach(labelsAsList -> {
                    Future<List<I18nLabel>> labInsertOutput = labelInsertService.submit(new I18nLabelInsertTask(i18nService, i18nModule.getId(), labelsAsList));
                    labelInsertOutputAsList.add(labInsertOutput);
                });
                List<List<I18nLabel>> i18nLabelsTotal = getAndAggregate(labelInsertOutputAsList,
                        " ERROR-INIT-I18nTranslations: Import of Labels of I18nModule " + i18nModule.getModuleName() + " failed ");
                i18nLabelsInsertedAsList.addAll(i18nLabelsTotal.stream().reduce(new ArrayList<>(), (a1, a2) -> {
                    a1.addAll(a2);
                    return a1;
                }, (a1, a2) -> {
                    a1.addAll(a2);
                    return a1;
                }));
            }
        }
        // Step: Assign I18nLabel ids to translations
        Map<String, I18nLabel> i18nLabelsInsertedMap = i18nLabelsInsertedAsList.stream()
                .collect(Collectors.toMap(I18nLabel::getResourceKey, Function.identity()));

        langImportDto.values().forEach(langDto -> {
            Optional.ofNullable(langDto.getInsertionsList()).ifPresent(ofInsertionsList -> {
                ofInsertionsList.forEach(i18nTranslation -> {
                    Optional.ofNullable(i18nLabelsInsertedMap.get(i18nTranslation.getI18nLabelResourceKey())).ifPresent(i18nLabel -> {
                        i18nTranslation.setI18nLabelId(i18nLabel.getId());
                    });
                });
            });
        });

        boolean hasUpdates = langImportDto.values().stream().anyMatch(I18nTranslationImportDTO::hasUpdates);
        if (hasUpdates) {
            // Step-2: Import i18nTranslations
            ExecutorService perLangService = Executors.newVirtualThreadPerTaskExecutor();
            List<Future<Integer>> langImportOutputAsList = new ArrayList<>();
            try (perLangService) {
                langImportDto.forEach((lang, langDto) -> {
                    // Assign insert of translations to a set of virtual threads, each inserting maximum TRANSLATIONS_PER_THREAD entries
                    List<List<I18nTranslation>> insertionsLists = CollectionUtil.splitList(langDto.getInsertionsList(), TRANSLATIONS_PER_THREAD);
                    List<List<I18nTranslation>> updateLists = CollectionUtil.splitList(langDto.getUpdatesList(), TRANSLATIONS_PER_THREAD);
                    List<List<Integer>> deletionsLists = CollectionUtil.splitList(langDto.getDeletionsList(), TRANSLATIONS_PER_THREAD);
                    int maxSize = Stream.of(insertionsLists.size(), updateLists.size(), deletionsLists.size())
                            .max(Comparator.naturalOrder()).get();
                    log.info(" INIT-I18nTranslations: Module={}-{}, Language={}-{}, maxSize={} ", i18nModule.getId(), i18nModule.getModuleName(), lang.getId(),
                            lang.getIsoCode(), maxSize);

                    for (int i = 0; i < maxSize; i++) {
                        List<I18nTranslation> insertionsList = Optional.ofNullable(CollectionUtil.safeGet(insertionsLists, i)).orElseGet(ArrayList::new);
                        List<I18nTranslation> updatesList = Optional.ofNullable(CollectionUtil.safeGet(updateLists, i)).orElseGet(ArrayList::new);
                        List<Integer> deletionsList = Optional.ofNullable(CollectionUtil.safeGet(deletionsLists, i)).orElseGet(ArrayList::new);
                        log.info(" INIT-I18nTranslations: Module={}-{}, Language={}-{}, Task-{} [Inserts={}, Updates={}, Deletions={}] ", i18nModule.getId(),
                                i18nModule.getModuleName(), lang.getId(), lang.getIsoCode(), i, insertionsList.size(), updatesList.size(),
                                deletionsList.size());

                        Future<Integer> langOutput = perLangService
                                .submit(new I18nTranslationInsertTask(i18nService, i18nModule.getId(), insertionsList, updatesList, deletionsList));
                        langImportOutputAsList.add(langOutput);
                    }
                });
                getAndAggregate(langImportOutputAsList, " ERROR-INIT-I18nTranslations: Imports of I18nModule " + i18nModule.getModuleName() + " failed ");
            }
        } // if (updates)
        // Step-3: Delete orphan labels
        i18nService.deleteI18nLabelsWithNoTranslationsByModuleId(i18nModule.getId());
        log.info(" INIT-I18nTranslations: Imports of I18nModule {}-{} completed ", i18nModule.getId(), i18nModule.getModuleName());
    }

    private String getAppResourceName(final String moduleName, final String langIso) {
        return RESOURCE_NAME_PATTERN.replace("{MODULE_NAME}", moduleName).replace("{LANG_ISO}", langIso);
    }

    protected class I18nLabelInsertTask implements Callable<List<I18nLabel>> {
        private I18nService i18nService;
        private Integer moduleId;
        private List<String> labelResKeys;

        public I18nLabelInsertTask(I18nService i18nService, Integer moduleId, List<String> labelResKeys) {
            super();
            this.i18nService = i18nService;
            this.moduleId = moduleId;
            this.labelResKeys = labelResKeys;
        }

        @Override
        public List<I18nLabel> call() throws Exception {
            List<I18nLabel> i18nLabels = this.i18nService.importI18nLabels(this.moduleId, this.labelResKeys);
            return i18nLabels;
        }
    }

    protected class I18nTranslationInsertTask implements Callable<Integer> {
        private I18nService i18nService;
        private Integer moduleId;
        private List<I18nTranslation> insertionsList;
        private List<I18nTranslation> updatesList;
        private List<Integer> deletionsList;

        public I18nTranslationInsertTask(I18nService i18nService, Integer moduleId, List<I18nTranslation> insertionsList, List<I18nTranslation> updatesList,
                                         List<Integer> deletionsList) {
            super();
            this.i18nService = i18nService;
            this.moduleId = moduleId;
            this.insertionsList = insertionsList;
            this.updatesList = updatesList;
            this.deletionsList = deletionsList;
        }

        @Override
        public Integer call() throws Exception {
            return i18nService.importI18nTranslations(this.moduleId, this.insertionsList, this.updatesList, this.deletionsList, false, false);
        }
    }

    private <T> List<T> getAndAggregate(final List<Future<T>> futuresAsList, final String errorMessageIfAtLeastOneFails) {
        List<T> outputAsList = new ArrayList<>();
        Exception exception = null;
        for (Future<T> futureAsRes : futuresAsList) {
            T futureOutput = null;
            try {
                futureOutput = futureAsRes.get();
                Optional.ofNullable(futureOutput).ifPresent(outputAsList::add);

            } catch (InterruptedException | ExecutionException e) {
                exception = e;
                break;
            }
        }
        Optional.ofNullable(exception).ifPresent(ofException -> {
            throw new IllegalStateException(errorMessageIfAtLeastOneFails, ofException);
        });
        return outputAsList;
    }
}
