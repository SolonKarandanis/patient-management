package com.pm.authservice.infrastructure.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@ConditionalOnExpression("${i18n.resources.DB.enabled:false}")
public class I18nInitTriggerBean {
    private final AtomicBoolean initInProgress = new AtomicBoolean(false);

    @Autowired
    private I18nInitService i18nInitService;

    @Async
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void triggerInitAsync() {
        if (!initInProgress.compareAndSet(false, true)) {
            log.warn(" TRIGGER-I18nTranslations: Already in progress on this node, skipping ");
            return;
        }
        try {
            i18nInitService.initI18nTranslations();
        } catch (Exception e) {
            log.error(" TRIGGER-I18nTranslations: Failed ", e);
        } finally {
            initInProgress.set(false);
        }
    }
}
