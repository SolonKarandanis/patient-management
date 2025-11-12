package com.pm.authservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="i18n_translations")
public class I18nTranslation {

    @Id
    @Column(name = "ID", unique = true, nullable = false)
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

    @Column(name = "TEXT_VALUE")
    private String textValue;

    @JoinColumn(
            name = "i18n_labels_id",
            referencedColumnName = "id",
            insertable = false,
            updatable = false
    )
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private I18nLabel i18nLabel;

    @Column(name = "I18N_LABELS_ID", insertable = true, updatable = true)
    private Integer i18nLabelId;

    @JoinColumn(
            name = "languages_id",
            referencedColumnName = "id",
            insertable = false,
            updatable = false
    )
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private Language language;

    @Column(name = "LANGUAGES_ID", insertable = true, updatable = true)
    private Integer languageId;

    @Column(name = "IS_USER_MODIFIED")
    private Boolean isUserModified;
}
