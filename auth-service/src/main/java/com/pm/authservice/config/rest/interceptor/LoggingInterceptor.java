package com.pm.authservice.config.rest.interceptor;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class LoggingInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(@NonNull HttpRequest request, byte @NonNull [] reqBody, ClientHttpRequestExecution execution)
            throws IOException {
        log.info("Request body: {}", new String(reqBody, StandardCharsets.UTF_8));
        ClientHttpResponse response = execution.execute(request, reqBody);
        InputStreamReader isr = new InputStreamReader(response.getBody(), StandardCharsets.UTF_8);
        String body = new BufferedReader(isr).lines().collect(Collectors.joining("\n"));
        log.info("Response body: {}", body);
        return response;
    }
}
