package com.pm.authservice.infrastructure.config.rest;

import com.pm.authservice.infrastructure.config.rest.interceptor.LoggingInterceptor;
import com.pm.authservice.infrastructure.web.exception.NotFoundException;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestTemplateConfig {

    @Bean
    RestClient restClient(HttpComponentsClientHttpRequestFactory clientHttpRequestFactory) {
        return RestClient.builder()
                .requestFactory(new BufferingClientHttpRequestFactory(clientHttpRequestFactory))
                .requestInterceptor(new LoggingInterceptor())
                .defaultStatusHandler(
                        HttpStatusCode::is4xxClientError,
                        (req, res) -> {
                            if (res.getStatusCode() == HttpStatus.NOT_FOUND) {
                                throw new NotFoundException("endpoint.not.found");
                            }
                        })
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (req, res) -> {})
                .build();
    }

    @Bean
    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory(CloseableHttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        return factory;
    }
}
