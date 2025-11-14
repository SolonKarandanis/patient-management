package com.pm.authservice.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnExpression("${i18n.resources.DB.enabled:false}")
public class I18nStartupBean implements SmartLifecycle {
    private boolean running = false;

    @Autowired
    private I18nInitService i18nInitService;

    @Override
    public void start() {
        log.info("-------> LIFECYCLE: I18nStartupBean.start ");
        try {
            i18nInitService.initI18nTranslations();
        } catch (Exception e) {
            log.error(" LIFECYCLE-EXCEPTION: ", e);
        }
        running = true;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void stop() {
        log.info("-------> LIFECYCLE: I18nStartupBean.stop-1 ");
        running = false;
    }

    @Override
    public void stop(Runnable callback) {
        log.info("-------> LIFECYCLE: I18nStartupBean.stop-2 ");
        stop();
        callback.run();
    }

    @Override
    public int getPhase() {
        return 0;
    }
}
