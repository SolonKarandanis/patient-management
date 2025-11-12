package com.pm.authservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name="i18n_modules")
public class I18nModule {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "module_name")
    private String moduleName;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "update_id")
    private String updateId;

    @Column(name = "update_start_time")
    private Date updateStartTime;

    @Column(name = "update_end_time")
    private Date updateEndTime;


}
