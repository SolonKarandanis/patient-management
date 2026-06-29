package com.pm.authservice.infrastructure.ai;

import com.pm.authservice.infrastructure.web.dto.ChatHistoryResponseDTO;
import com.pm.authservice.infrastructure.web.dto.ChatResponseDTO;
import com.pm.authservice.infrastructure.web.dto.ChatServiceRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class AiServiceClientBean implements AiServiceClient {

    @Value("${ai.service.protocol}")
    private String protocol;

    @Value("${ai.service.host}")
    private String host;

    @Value("${ai.service.port}")
    private String port;

    @Value("${ai.service.context}")
    private String context;

    private final RestClient restClient;

    public AiServiceClientBean(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public ChatResponseDTO chat(ChatServiceRequestDTO request) {
        return restClient.post()
                .uri(endpoint() + "/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ChatResponseDTO.class);
    }

    @Override
    public ChatHistoryResponseDTO getHistory(String sessionId) {
        return restClient.get()
                .uri(endpoint() + "/chat/{sessionId}/history", sessionId)
                .retrieve()
                .body(ChatHistoryResponseDTO.class);
    }

    @Override
    public void clearSession(String sessionId) {
        restClient.delete()
                .uri(endpoint() + "/chat/{sessionId}", sessionId)
                .retrieve()
                .toBodilessEntity();
    }

    private String endpoint() {
        return protocol + "://" + host + ":" + port + "/" + context;
    }
}
