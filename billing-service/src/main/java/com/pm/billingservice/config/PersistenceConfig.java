package com.pm.billingservice.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class PersistenceConfig {

    @Bean
    public DataSource dataSource() {
        HikariDataSource datasource = new HikariDataSource();
        datasource.setAutoCommit(false); //Default
        datasource.addDataSourceProperty("rewriteBatchedStatements", true);
        datasource.addDataSourceProperty("dataSource.cachePrepStmts", "true");
        datasource.addDataSourceProperty("dataSource.prepStmtCacheSize", "250");
        datasource.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", "2048");
        datasource.addDataSourceProperty("dataSource.useServerPrepStmts", "true");
        return datasource;
    }
}
