package com.pm.authservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NamedQuery(name = I18nLabel.GET_I18N_LABELS,
        query = "SELECT lab " +
                "FROM I18nLabel lab " +
                "ORDER BY lab.id ASC")
@NamedQuery(name = I18nLabel.GET_I18N_LABELS_BY_MODULE_ID,
        query = "SELECT lab " +
                "FROM I18nLabel lab " +
                "WHERE lab.i18nModuleId = :moduleId " +
                "ORDER BY lab.id ASC")
@NamedQuery(name = I18nLabel.GET_I18N_LABEL_RESOURCE_KEYS,
        query = "SELECT lab " +
                "FROM I18nLabel lab " +
                "ORDER BY lab.resourceKey ASC")
@NamedQuery(name = I18nLabel.DELETE_I18N_LABEL_BY_ID,
        query = "DELETE FROM I18nLabel lab " +
                "WHERE lab.id = :labelId")
@NamedQuery(name = I18nLabel.GET_COUNT_OF_I18N_LABELS_WITH_NO_TRANSLATIONS_BY_MODULE_ID,
        query = "SELECT COUNT(lab.id) " +
                "FROM I18nLabel lab " +
                "WHERE lab.i18nModuleId = :moduleId " +
                "AND NOT EXISTS (SELECT trn FROM I18nTranslation trn WHERE trn.i18nLabelId = lab.id)")
@NamedQuery(name = I18nLabel.DELETE_I18N_LABELS_WITH_NO_TRANSLATIONS_BY_MODULE_ID,
        query = "DELETE FROM I18nLabel lab " +
                "WHERE lab.i18nModuleId = :moduleId " +
                "AND NOT EXISTS (SELECT trn FROM I18nTranslation trn WHERE trn.i18nLabelId = lab.id)")
@Entity
@Table(name="i18n_labels")
public class I18nLabel {

    public static final String GET_I18N_LABELS = "I18nLabel.getI18nLabels";
    public static final String GET_I18N_LABELS_BY_MODULE_ID = "I18nLabel.getI18nLabelsByModuleId";
    public static final String GET_I18N_LABEL_RESOURCE_KEYS = "I18nLabel.getI18nLabelResourceKeys";
    public static final String DELETE_I18N_LABEL_BY_ID = "I18nLabel.deleteI18nLabelById";
    public static final String GET_COUNT_OF_I18N_LABELS_WITH_NO_TRANSLATIONS_BY_MODULE_ID = "I18nLabel.getCountOfI18nLabelsWithNoTranslationsByModuleId";
    public static final String DELETE_I18N_LABELS_WITH_NO_TRANSLATIONS_BY_MODULE_ID = "I18nLabel.deleteI18nLabelsWithNoTranslationsByModuleId";

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "i18nLabelsGenerator"
    )
    @SequenceGenerator(
            name = "i18nLabelsGenerator",
            sequenceName = "i18n_labels_seq",
            allocationSize = 1,
            initialValue = 1
    )
    private Integer id;

    @Column(name = "resource_key")
    private String resourceKey;

    @JoinColumn(
            name = "i18n_modules_id",
            referencedColumnName = "id",
            insertable = false,
            updatable = false
    )
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private I18nModule i18nModule;

    @Column(name = "i18n_modules_id", insertable = true, updatable = true)
    private Integer i18nModuleId;
}
