package com.pm.aiservice.service;

import com.pm.aiservice.domain.model.ChatMessage;
import com.pm.aiservice.domain.model.Role;
import com.pm.aiservice.domain.port.LlmPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final LlmPort llmPort;
    private final ChatMemory chatMemory;

    public String chat(String sessionId, String userMessage) {
        log.debug("Processing chat request for session: {}", sessionId);
        return llmPort.chat(sessionId, userMessage);
    }

    public Flux<String> streamChat(String sessionId, String userMessage) {
        log.debug("Processing streaming chat request for session: {}", sessionId);
        return llmPort.streamChat(sessionId, userMessage);
    }

    public int getSessionTurnCount(String sessionId) {
        return chatMemory.get(sessionId).size() / 2;
    }

    public void clearSession(String sessionId) {
        log.debug("Clearing chat session: {}", sessionId);
        chatMemory.clear(sessionId);
    }

    public boolean sessionExists(String sessionId) {
        return !chatMemory.get(sessionId).isEmpty();
    }

    public List<ChatMessage> getConversationHistory(String sessionId) {
        return chatMemory.get(sessionId).stream()
                .map(this::toDomainMessage)
                .filter(msg -> msg != null && msg.getRole() != Role.SYSTEM)
                .collect(Collectors.toList());
    }

    private ChatMessage toDomainMessage(Message message) {
        if (message == null) return null;
        return switch (message) {
            case UserMessage um -> new ChatMessage(Role.USER, um.getText());
            case AssistantMessage am -> new ChatMessage(Role.ASSISTANT, am.getText());
            case SystemMessage sm -> new ChatMessage(Role.SYSTEM, sm.getText());
            default -> null;
        };
    }
}
