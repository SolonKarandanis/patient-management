package com.pm.notificationservice.config;


import org.springframework.boot.jpa.autoconfigure.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.context.annotation.Configuration;

@Configuration
class LiquibaseJpaDependencyConfiguration extends EntityManagerFactoryDependsOnPostProcessor {

    LiquibaseJpaDependencyConfiguration() {
        super("liquibase");
    }
}
