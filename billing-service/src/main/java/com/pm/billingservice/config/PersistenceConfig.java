package com.pm.billingservice.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class PersistenceConfig {

    @Value("${datasource.url}")
    private String dsUrl;

    @Value("${datasource.username}")
    private String dsUsername;

    @Value("${datasource.username}")
    private String dsPassword;

    @Bean
    public DataSource dataSource() {
        HikariDataSource datasource = new HikariDataSource();
        datasource.setAutoCommit(false); //Default
        datasource.setJdbcUrl(dsUrl);
        datasource.setPassword(dsUsername);
        datasource.setUsername(dsPassword);
        datasource.addDataSourceProperty("rewriteBatchedStatements", true);
        datasource.addDataSourceProperty("dataSource.cachePrepStmts", "true");
        datasource.addDataSourceProperty("dataSource.prepStmtCacheSize", "250");
        datasource.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", "2048");
        datasource.addDataSourceProperty("dataSource.useServerPrepStmts", "true");
        return datasource;
    }
}
