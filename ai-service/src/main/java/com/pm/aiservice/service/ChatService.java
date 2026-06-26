package com.pm.aiservice.service;

import com.pm.aiservice.domain.model.ChatMessage;
import com.pm.aiservice.domain.model.Role;
import com.pm.aiservice.domain.port.LlmPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final LlmPort llmPort;

    private final ConcurrentHashMap<String, List<ChatMessage>> sessions = new ConcurrentHashMap<>();

    public String chat(String sessionId, String userMessage) {
        List<ChatMessage> history = sessions.computeIfAbsent(sessionId, id -> new ArrayList<>());
        history.add(new ChatMessage(Role.USER, userMessage));

        String reply = llmPort.chat(history);

        history.add(new ChatMessage(Role.ASSISTANT, reply));
        return reply;
    }

    public Flux<String> streamChat(String sessionId, String userMessage) {
        List<ChatMessage> history = sessions.computeIfAbsent(sessionId, id -> new ArrayList<>());
        history.add(new ChatMessage(Role.USER, userMessage));

        StringBuilder fullReply = new StringBuilder();
        return llmPort.streamChat(history)
                .doOnNext(fullReply::append)
                .doOnComplete(() -> history.add(new ChatMessage(Role.ASSISTANT, fullReply.toString())));
    }

    public int getSessionTurnCount(String sessionId) {
        List<ChatMessage> history = sessions.get(sessionId);
        if (history == null) return 0;
        return history.size() / 2;
    }

    public void clearSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public boolean sessionExists(String sessionId) {
        return sessions.containsKey(sessionId);
    }
}
