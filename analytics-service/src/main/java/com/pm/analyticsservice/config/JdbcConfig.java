package com.pm.analyticsservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.relational.core.dialect.Dialect;

@Configuration
public class JdbcConfig {

    @Bean
    public Dialect clickHouseDialect() {
        return new ClickHouseDialect();
    }
}
