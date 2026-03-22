package com.pm.fts.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest5_client.low_level.Rest5Client;
import lombok.extern.java.Log;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchClients;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.elasticsearch.support.HttpHeaders;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;

@Log
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.pm.fts.repository")
@ComponentScan(basePackages = { "com.pm.fts" })
public class ElasticsearchClientConfig extends ElasticsearchConfiguration {

    @Value("${document.index.pfx}")
    public String indexPrefix;

    @Value("${elasticsearch.host}")
    public String elasticsearchHost;

    @Value("${elasticsearch.port}")
    public int elasticsearchPort;

    @Value("${elasticsearch.scheme}")
    public String elasticsearchScheme;

    @Value("${elasticsearch.username}")
    public String elasticsearchUsername;

    @Value("${elasticsearch.password}")
    public String elasticsearchPassword;

    @Value("${es.ca-fingerprint}")
    public String caFingerprint;

    @Bean
    String indexPrefix() {
        return indexPrefix;
    }

    @Override
    public ClientConfiguration clientConfiguration() {
        log.info("--------> elasticsearchHost: " + elasticsearchHost);
        log.info("--------> elasticsearchPort: " + elasticsearchPort);
        log.info("--------> elasticsearchScheme: " + elasticsearchScheme);
        HttpHeaders defaultHeaders = new HttpHeaders();
        defaultHeaders.add("Content-type", "application/json");
        ClientConfiguration.MaybeSecureClientConfigurationBuilder builder = ClientConfiguration.builder()
                .connectedTo(elasticsearchHost + ":" + elasticsearchPort);
        if ("https".equalsIgnoreCase(elasticsearchScheme)) {
            // 1. SSL Configuration
            if (StringUtils.hasText(caFingerprint)) {
                // ES 8 approach: Verify via CA Fingerprint
                log.info("--------> ES 8 approach: ");
                builder.usingSsl(caFingerprint);
            } else {
                log.info("--------> ES 7 approach: ");
                SSLContext sslContext = getTrustAllSslContext();
                builder.usingSsl(sslContext, NoopHostnameVerifier.INSTANCE);
            }
        }
        // 2. Authentication Configuration
        if (StringUtils.hasText(elasticsearchUsername) && StringUtils.hasText(elasticsearchPassword)) {
            builder.withBasicAuth(elasticsearchUsername, elasticsearchPassword);
        }

        return builder.build();
    }

    @Override
    @Bean
    public ElasticsearchTransport elasticsearchTransport(@NonNull Rest5Client rest5Client, JsonpMapper jsonpMapper) {
        return ElasticsearchClients.getElasticsearchTransport(rest5Client, "imperative", this.transportOptions(), jsonpMapper);
    }

    @Override
    @Bean
    public ElasticsearchClient elasticsearchClient(@NonNull ElasticsearchTransport transport) {
        return new ElasticsearchClient(transport);
    }

    protected SSLContext getTrustAllSslContext() {
        try {
            return SSLContextBuilder.create()
                    .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                    .build();
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            log.log(Level.SEVERE, "Caught Error: {}", e.getMessage());
        }
        return null;
    }

    private SSLContext getSSLContext() {
        try {
            SSLContextBuilder sslBuilder = SSLContexts.custom().loadTrustMaterial(null, (x509Certificates, s) -> true);
            return sslBuilder.build();
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            log.log(Level.SEVERE, "Caught Error: {}", e.getMessage());
        }
        return null;

    }
}
