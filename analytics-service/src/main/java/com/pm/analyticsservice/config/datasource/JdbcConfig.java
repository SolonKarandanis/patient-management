package com.pm.analyticsservice.config.datasource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.relational.core.dialect.Dialect;

import javax.sql.DataSource;

@Configuration
public class JdbcConfig {

    @Bean
    public Dialect clickHouseDialect() {
        return new ClickHouseDialect();
    }
    
    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }
}
