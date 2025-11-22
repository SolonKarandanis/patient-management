package com.pm.authservice.i18n.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NamedQuery(name = Language.GET_LANGUAGES,
        query = "SELECT lang " +
                "FROM Language lang " +
                "ORDER BY lang.id ASC")
@Entity
@Table(name="languages")
public class Language {

    public static final String GET_LANGUAGES = "Language.getLanguages";

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "language_name")
    private String languageName;

    @Column(name = "language_key")
    private String languageKey;

    @Column(name = "iso_code")
    private String isoCode;

    @Column(name = "sort_key")
    private Short sortKey;

    @Column(name = "is_enabled")
    private Boolean isEnabled;
}
