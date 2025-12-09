package com.pm.analyticsservice.config.datasource;


import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class BatchDataSourceConfig {

    @Bean("batchDataSource")
    public DataSource batchDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.h2.Driver")
                .url("jdbc:h2:file:./data/batchdb;DB_CLOSE_ON_EXIT=FALSE") // Persistent H2 file database
                .username("sa")
                .password("")
                .build();
    }
}
