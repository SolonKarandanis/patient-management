package com.pm.authservice.i18n.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NamedQuery(name = I18nModule.GET_I18N_MODULES,
        query = "SELECT mod " +
                "FROM I18nModule mod " +
                "ORDER BY mod.id ASC")
@NamedQuery(name = I18nModule.GET_ACTIVE_I18N_MODULES,
        query = "SELECT mod " +
                "FROM I18nModule mod " +
                "WHERE mod.isActive = true " +
                "ORDER BY mod.id ASC")
@NamedQuery(name = I18nModule.LOCK_UPDATE_MODULE_BY_ID,
        query = "UPDATE I18nModule mod " +
                "SET mod.updateId = :updateId, mod.updateStartTime = :updStartTime, mod.updateEndTime = NULL " +
                "WHERE mod.id = :moduleId " +
                "AND (" +
                "   (mod.updateId IS NULL AND mod.updateStartTime IS NULL AND mod.updateEndTime IS NULL) " +
                "   OR (mod.updateId IS NOT NULL AND mod.updateStartTime IS NOT NULL AND mod.updateEndTime IS NOT NULL)" +
                ")"
)
@NamedQuery(name = I18nModule.UNLOCK_UPDATE_MODULE_BY_ID,
        query = "UPDATE I18nModule mod " +
                "SET mod.updateEndTime = :updEndTime " +
                "WHERE mod.id = :moduleId " +
                "AND mod.updateId = :updateId")
@Entity
@Table(name="i18n_modules")
public class I18nModule {

    public static final String GET_I18N_MODULES = "I18nModule.getI18nModules";
    public static final String GET_ACTIVE_I18N_MODULES = "I18nModule.getActiveI18nModules";
    public static final String LOCK_UPDATE_MODULE_BY_ID = "I18nModule.lockUpdateModuleById";
    public static final String UNLOCK_UPDATE_MODULE_BY_ID = "I18nModule.unlockUpdateModuleById";

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

    @Transient
    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }
}
