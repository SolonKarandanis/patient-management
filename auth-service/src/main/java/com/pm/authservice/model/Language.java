package com.pm.authservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="languages")
public class Language {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "language_name")
    private String languageName;

    @Column(name = "language_key")
    private String languageKey;

    @Column(name = "iso_code")
    private String isoCode;
}
