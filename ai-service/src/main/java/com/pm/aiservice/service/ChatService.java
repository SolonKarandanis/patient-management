package com.pm.aiservice.service;

import com.pm.aiservice.domain.model.ChatMessage;
import com.pm.aiservice.domain.model.Role;
import com.pm.aiservice.domain.port.LlmPort;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final LlmPort llmPort;
    private final ChatMemory chatMemory;

    public String chat(String sessionId, String userMessage) {
        chatMemory.add(sessionId, List.of(new UserMessage(userMessage)));

        List<ChatMessage> history = getHistory(sessionId);
        String reply = llmPort.chat(history);

        chatMemory.add(sessionId, List.of(new AssistantMessage(reply)));
        return reply;
    }

    public Flux<String> streamChat(String sessionId, String userMessage) {
        chatMemory.add(sessionId, List.of(new UserMessage(userMessage)));

        List<ChatMessage> history = getHistory(sessionId);
        StringBuilder fullReply = new StringBuilder();

        return llmPort.streamChat(history)
                .doOnNext(fullReply::append)
                .doOnComplete(() ->
                        chatMemory.add(sessionId, List.of(new AssistantMessage(fullReply.toString()))));
    }

    public int getSessionTurnCount(String sessionId) {
        return chatMemory.get(sessionId).size() / 2;
    }

    public void clearSession(String sessionId) {
        chatMemory.clear(sessionId);
    }

    public boolean sessionExists(String sessionId) {
        return !chatMemory.get(sessionId).isEmpty();
    }

    private List<ChatMessage> getHistory(String sessionId) {
        return chatMemory.get(sessionId).stream()
                .map(this::toDomainMessage)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private ChatMessage toDomainMessage(Message message) {
        return switch (message) {
            case UserMessage um -> new ChatMessage(Role.USER, um.getText());
            case AssistantMessage am -> new ChatMessage(Role.ASSISTANT, am.getText());
            case SystemMessage sm -> new ChatMessage(Role.SYSTEM, sm.getText());
            default -> null;
        };
    }
}
