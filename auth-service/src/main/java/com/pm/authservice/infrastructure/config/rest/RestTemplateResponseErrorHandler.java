package com.pm.authservice.infrastructure.config.rest;

import com.pm.authservice.infrastructure.web.exception.NotFoundException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return (httpResponse.getStatusCode().is4xxClientError()
                || httpResponse.getStatusCode().is5xxServerError()
        );
    }

    @Override
    public void handleError(@NonNull URI url, @NonNull HttpMethod method, ClientHttpResponse httpResponse) throws IOException {
        if (httpResponse.getStatusCode().is5xxServerError()) {
            // handle SERVER_ERROR
        } else if (httpResponse.getStatusCode().is4xxClientError()) {
            // handle CLIENT_ERROR
            if (httpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException("enpoint.not.found");
            }
        }
    }
}
