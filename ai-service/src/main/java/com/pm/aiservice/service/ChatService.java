package com.pm.aiservice.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final AnthropicClient anthropicClient;

    @Value("${anthropic.model}")
    private String model;

    @Value("${anthropic.max-tokens}")
    private long maxTokens;

    // In-memory conversation history keyed by session ID
    private final ConcurrentHashMap<String, List<MessageParam>> sessions = new ConcurrentHashMap<>();

    public String chat(String sessionId, String userMessage) {
        List<MessageParam> history = sessions.computeIfAbsent(sessionId, id -> new ArrayList<>());

        history.add(MessageParam.builder()
                .role(MessageParam.Role.USER)
                .content(userMessage)
                .build());

        Message response = anthropicClient.messages().create(
                MessageCreateParams.builder()
                        .model(model)
                        .maxTokens(maxTokens)
                        .messages(history)
                        .build()
        );

        String assistantText = response.content().stream()
                .flatMap(block -> block.text().stream())
                .map(TextBlock::text)
                .collect(Collectors.joining());

        history.add(MessageParam.builder()
                .role(MessageParam.Role.ASSISTANT)
                .content(assistantText)
                .build());

        return assistantText;
    }

    public int getSessionTurnCount(String sessionId) {
        List<MessageParam> history = sessions.get(sessionId);
        if (history == null) return 0;
        // Each turn is one user + one assistant message
        return history.size() / 2;
    }

    public void clearSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public boolean sessionExists(String sessionId) {
        return sessions.containsKey(sessionId);
    }
}
