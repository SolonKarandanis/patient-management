package com.pm.authservice.repository;


import com.pm.authservice.model.I18nLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface I18nLabelRepository extends JpaRepository<I18nLabel, Integer> {

    @Query(name =I18nLabel.GET_I18N_LABELS)
    List<I18nLabel> getI18nLabels();

    @Query(name =I18nLabel.GET_I18N_LABELS_BY_MODULE_ID)
    List<I18nLabel> getI18nLabelsByModuleId(@Param("moduleId") Integer moduleId);

    @Query(name =I18nLabel.GET_I18N_LABEL_RESOURCE_KEYS)
    List<String> getI18nLabelResourceKeys();

    @Modifying
    @Query(name =I18nLabel.DELETE_I18N_LABEL_BY_ID)
    int deleteI18nLabelById(@Param("labelId") Integer labelId);

    @Query(name = I18nLabel.GET_COUNT_OF_I18N_LABELS_WITH_NO_TRANSLATIONS_BY_MODULE_ID)
    Integer getCountOfI18nLabelsWithNoTranslationsByModuleId(@Param("moduleId") Integer moduleId);

    @Modifying
    @Query(name =I18nLabel.DELETE_I18N_LABELS_WITH_NO_TRANSLATIONS_BY_MODULE_ID)
    int deleteI18nLabelsWithNoTranslationsByModuleId(@Param("moduleId") Integer moduleId);
}
