package com.pm.authservice.repository;


import com.pm.authservice.model.I18nLabel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = "SELECT DISTINCT lab.id "
            + "FROM I18nLabel lab JOIN lab.i18nModule mod LEFT JOIN I18nTranslation trl ON trl.i18nLabelId = lab.id "
            + "WHERE lab.id IN ( "
            + "SELECT lab2.id FROM I18nLabel lab2 LEFT JOIN I18nTranslation trl2 ON trl2.i18nLabelId = lab2.id "
            + "WHERE (:term IS NULL OR :term = '' OR LOWER(trl2.textValue) LIKE LOWER(CONCAT('%', :term, '%')) "
            + "OR LOWER(lab2.resourceKey) LIKE LOWER(CONCAT('%', :term, '%'))) "
            + "AND (:langId IS NULL OR trl2.languageId = :langId)) AND (:modId IS NULL OR mod.id = :modId) "
            + "ORDER BY lab.id ASC",
            countQuery = "SELECT COUNT(DISTINCT lab.id) "
                    + "FROM I18nLabel lab JOIN lab.i18nModule mod LEFT JOIN I18nTranslation trl ON trl.i18nLabelId = lab.id "
                    + "WHERE lab.id IN ( "
                    + "SELECT lab2.id FROM I18nLabel lab2 LEFT JOIN I18nTranslation trl2 ON trl2.i18nLabelId = lab2.id "
                    + "WHERE (:term IS NULL OR :term = '' OR LOWER(trl2.textValue) LIKE LOWER(CONCAT('%', :term, '%')) "
                    + "OR LOWER(lab2.resourceKey) LIKE LOWER(CONCAT('%', :term, '%'))) "
                    + "AND (:langId IS NULL OR trl2.languageId = :langId)) AND (:modId IS NULL OR mod.id = :modId) ")
    Page<Long> searchI18nResourcesDistinctLabelIds(
            @Param("langId") Integer langId, @Param("modId") Integer modId, @Param("term") String term, Pageable pageable);

    @Query("SELECT lab.id, lab.resourceKey, mod.moduleName, trl.languageId, trl.textValue "
            + "FROM I18nLabel lab JOIN lab.i18nModule mod LEFT JOIN I18nTranslation trl ON trl.i18nLabelId = lab.id "
            + "WHERE lab.id IN :ids ORDER BY lab.id ASC")
    List<Object[]> getResourceDataWithPaginationByLabelIds(@Param("ids") List<Long> ids);
}
