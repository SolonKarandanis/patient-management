package com.pm.authservice.i18n.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NamedQuery(name = I18nTranslation.GET_TRANSLATION_RECORDS_BY_MODULE_NAME_AND_LANG_ISO_CODE,
        query = "SELECT lab.id, lab.resourceKey, trn.id, trn.textValue " +
                "FROM I18nTranslation trn " +
                "LEFT OUTER JOIN trn.i18nLabel lab "
                + "WHERE trn.languageId = (SELECT lang.id FROM Language lang WHERE lang.isoCode = :langIso) "
                + "AND lab.i18nModuleId = (SELECT mod.id FROM I18nModule mod WHERE mod.moduleName = :moduleName) ORDER BY lab.id ASC, trn.id ASC")
@NamedQuery(name = I18nTranslation.GET_TRANSLATION_RECORDS_BY_MODULE_ID_AND_LANG_ID_AND_NO_USER_MODIFIED,
        query = "SELECT trn " +
                "FROM I18nTranslation trn " +
                "LEFT JOIN FETCH trn.i18nLabel lab "
                + "WHERE trn.languageId = :langId AND lab.i18nModuleId = :moduleId AND (trn.isUserModified IS NULL OR trn.isUserModified = false) "
                + "ORDER BY lab.id ASC, trn.id ASC")
@NamedQuery(name = I18nTranslation.GET_LABEL_KEYS_HAVING_USER_MODIFIED_TRANSLATION_BY_MODULE_ID_AND_LANG_ID,
        query = "SELECT lab.resourceKey " +
                "FROM I18nLabel lab " +
                "WHERE lab.i18nModuleId = :moduleId " +
                "AND EXISTS (SELECT trn.id FROM I18nTranslation trn "
                + "WHERE trn.i18nLabelId = lab.id AND trn.languageId = :langId AND trn.isUserModified = true) " +
                "ORDER BY lab.resourceKey ASC")
@NamedQuery(name = I18nTranslation.DELETE_I18N_TRANSLATION_BY_ID,
        query = "DELETE FROM I18nTranslation trn " +
                "WHERE trn.id = :translationId")
@NamedQuery(name = I18nTranslation.DELETE_I18N_TRANSLATION_BY_IDS,
        query = "DELETE FROM I18nTranslation trn " +
                "WHERE trn.id IN (:translationIds)")
@NamedQuery(name = I18nTranslation.GET_TRANSLATIONS_BY_RECORDS_BY_RESOURCE_IDS,
        query = "SELECT trn " +
                "FROM I18nTranslation trn " +
                "LEFT JOIN FETCH trn.language lang " +
                "LEFT JOIN FETCH trn.i18nLabel lab " +
                "LEFT JOIN FETCH lab.i18nModule mod " +
                "WHERE lab.id in (:ids)")
@Entity
@Table(name="i18n_translations")
public class I18nTranslation {

    public static final String GET_TRANSLATION_RECORDS_BY_MODULE_NAME_AND_LANG_ISO_CODE= "I18nTranslation.getTranslationRecordsByModuleNameAndLangIsoCode";
    public static final String GET_TRANSLATION_RECORDS_BY_MODULE_ID_AND_LANG_ID_AND_NO_USER_MODIFIED = "I18nTranslation.getTranslationRecordsByModuleIdAndLangIdAndNoUserModified";
    public static final String GET_LABEL_KEYS_HAVING_USER_MODIFIED_TRANSLATION_BY_MODULE_ID_AND_LANG_ID= "I18nTranslation.getLabelKeysHavingUserModifiedTranslationByModuleIdAndLangId";
    public static final String DELETE_I18N_TRANSLATION_BY_ID = "I18nTranslation.deleteI18nTranslationById";
    public static final String DELETE_I18N_TRANSLATION_BY_IDS = "I18nTranslation.deleteI18nTranslationByIds";
    public static final String GET_TRANSLATIONS_BY_RECORDS_BY_RESOURCE_IDS = "I18nTranslation.getTranslationsByResourceIds";

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "i18nTranslationGenerator"
    )
    @SequenceGenerator(
            name = "i18nTranslationGenerator",
            sequenceName = "i18n_translations_seq",
            allocationSize = 1,
            initialValue = 1
    )
    private Integer id;

    @Column(name = "text_value")
    private String textValue;

    @JoinColumn(
            name = "i18n_labels_id",
            referencedColumnName = "id",
            insertable = false,
            updatable = false
    )
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private I18nLabel i18nLabel;

    @Column(name = "i18n_labels_id", insertable = true, updatable = true)
    private Integer i18nLabelId;

    @JoinColumn(
            name = "languages_id",
            referencedColumnName = "id",
            insertable = false,
            updatable = false
    )
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private Language language;

    @Column(name = "languages_id", insertable = true, updatable = true)
    private Integer languageId;

    @Column(name = "is_user_modified")
    private Boolean isUserModified;

    @Transient
    private String i18nLabelResourceKey;
}
