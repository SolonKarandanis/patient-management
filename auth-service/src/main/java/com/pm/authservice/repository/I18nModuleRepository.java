package com.pm.authservice.repository;

import com.pm.authservice.model.I18nModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface I18nModuleRepository extends JpaRepository<I18nModule, Integer> {

    @Query(name = I18nModule.GET_I18N_MODULES)
    List<I18nModule> getI18nModules();

    @Query(name = I18nModule.GET_ACTIVE_I18N_MODULES)
    List<I18nModule> getActiveI18nModules();

    @Modifying
    @Query(name = I18nModule.LOCK_UPDATE_MODULE_BY_ID)
    int lockUpdateModuleById(@Param("moduleId") Integer moduleId, @Param("updateId") String updateId, @Param("updStartTime") Date updStartTime);

    @Modifying
    @Query(name = I18nModule.UNLOCK_UPDATE_MODULE_BY_ID)
    int unlockUpdateModuleById(@Param("moduleId") Integer moduleId, @Param("updateId") String updateId, @Param("updEndTime") Date updEndTime);
}
