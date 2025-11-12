package com.pm.authservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="i18n_labels")
public class I18nLabel {

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
