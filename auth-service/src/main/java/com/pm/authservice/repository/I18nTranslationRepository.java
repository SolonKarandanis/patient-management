package com.pm.authservice.repository;

import com.pm.authservice.model.I18nTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface I18nTranslationRepository extends JpaRepository<I18nTranslation, Integer> {

    @Query(name = I18nTranslation.GET_TRANSLATION_RECORDS_BY_MODULE_NAME_AND_LANG_ISO_CODE)
    List<Object[]> getTranslationRecordsByModuleNameAndLangIsoCode(@Param("moduleName") String moduleName, @Param("langIso") String langIso);

    @Query(name = I18nTranslation.GET_TRANSLATION_RECORDS_BY_MODULE_ID_AND_LANG_ID_AND_NO_USER_MODIFIED)
    List<I18nTranslation> getTranslationRecordsByModuleIdAndLangIdAndNoUserModified(@Param("moduleId") Integer moduleId,
                                                                                           @Param("langId") Integer langId);

    @Query(name = I18nTranslation.GET_LABEL_KEYS_HAVING_USER_MODIFIED_TRANSLATION_BY_MODULE_ID_AND_LANG_ID)
    List<String> getLabelKeysHavingUserModifiedTranslationByModuleIdAndLangId(@Param("moduleId") Integer moduleId, @Param("langId") Integer langId);

    default Map<String, String> getTranslationsByModuleAndLangIsoCode(String moduleName, String langIso) {
        List<Object[]> obRecords = getTranslationRecordsByModuleNameAndLangIsoCode(moduleName, langIso);
        Map<String, String> outputAsMap = new LinkedHashMap<>();
        obRecords.forEach(ob -> {
            outputAsMap.put((String) ob[1], (String) ob[3]);
        });
        return outputAsMap;
    }

    @Modifying
    @Query(name = I18nTranslation.DELETE_I18N_TRANSLATION_BY_ID)
    int deleteI18nTranslationById(@Param("translationId") Integer translationId);

    @Modifying
    @Query(name = I18nTranslation.DELETE_I18N_TRANSLATION_BY_IDS)
    int deleteI18nTranslationByIds(@Param("translationIds") List<Integer> translationIds);

}
