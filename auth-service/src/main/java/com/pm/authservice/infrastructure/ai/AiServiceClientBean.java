package com.pm.authservice.infrastructure.ai;

import com.pm.authservice.infrastructure.web.dto.ChatHistoryResponseDTO;
import com.pm.authservice.infrastructure.web.dto.ChatResponseDTO;
import com.pm.authservice.infrastructure.web.dto.ChatServiceRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AiServiceClientBean implements AiServiceClient{

    @Value("${ai.service.protocol}")
    private String protocol;

    @Value("${ai.service.host}")
    private String host;

    @Value("${ai.service.port}")
    private String port;

    @Value("${ai.service.context}")
    private String context;

    private final RestTemplate restTemplate;

    public AiServiceClientBean(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ChatResponseDTO chat(ChatServiceRequestDTO request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ChatServiceRequestDTO> entity = new HttpEntity<>(request, headers);
        return restTemplate.postForObject(endpoint() + "/chat", entity, ChatResponseDTO.class);
    }

    @Override
    public ChatHistoryResponseDTO getHistory(String sessionId) {
        return restTemplate.getForObject(endpoint() + "/chat/" + sessionId + "/history", ChatHistoryResponseDTO.class);
    }

    @Override
    public void clearSession(String sessionId) {
        restTemplate.delete(endpoint() + "/chat/" + sessionId);
    }

    private String endpoint() {
        return protocol + "://" + host + ":" + port + "/" + context;
    }
}
