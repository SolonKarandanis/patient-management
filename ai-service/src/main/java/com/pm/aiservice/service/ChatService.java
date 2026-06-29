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
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final LlmPort llmPort;
    private final ChatMemory chatMemory;
    private final VectorStore vectorStore;

    private static final int RAG_TOP_K = 3;

    private static final String SYSTEM_PROMPT = """
            You are a support assistant for the Patient Management System.
            Help users navigate the application, understand its features, and answer questions about managing patients, \
            billing, analytics, notifications, and user accounts.
            Be concise and helpful. If you don't know something, say so — do not invent features.
            When relevant documentation is provided in [CONTEXT], use it to answer accurately.
            """;

    public String chat(String sessionId, String userMessage) {
        if (!sessionExists(sessionId)) {
            chatMemory.add(sessionId, List.of(new SystemMessage(SYSTEM_PROMPT)));
        }
        chatMemory.add(sessionId, List.of(new UserMessage(userMessage)));

        List<ChatMessage> messages = buildMessagesWithRag(sessionId, userMessage);
        String reply = llmPort.chat(messages);

        chatMemory.add(sessionId, List.of(new AssistantMessage(reply)));
        return reply;
    }

    public Flux<String> streamChat(String sessionId, String userMessage) {
        chatMemory.add(sessionId, List.of(new UserMessage(userMessage)));

        List<ChatMessage> messages = buildMessagesWithRag(sessionId, userMessage);
        StringBuilder fullReply = new StringBuilder();

        return llmPort.streamChat(messages)
                .doOnNext(fullReply::append)
                .doOnComplete(() ->
                        chatMemory.add(sessionId, List.of(new AssistantMessage(fullReply.toString()))));
    }

    private List<ChatMessage> buildMessagesWithRag(String sessionId, String userMessage) {
        List<ChatMessage> history = getHistory(sessionId);
        String context = retrieveContext(userMessage);
        if (context == null) {
            return history;
        }
        // Replace the last message (current user turn already stored in memory) with a
        // context-augmented version for the LLM call only — memory keeps the raw message.
        List<ChatMessage> messages = new ArrayList<>(history.subList(0, history.size() - 1));
        messages.add(new ChatMessage(Role.USER,
                "[CONTEXT]\n" + context + "\n[/CONTEXT]\n\n" + userMessage));
        return messages;
    }

    private String retrieveContext(String query) {
        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(RAG_TOP_K).build()
        );
        if (docs.isEmpty()) {
            log.debug("RAG: no relevant chunks found for query: {}", query);
            return null;
        }
        log.debug("RAG: injecting {} chunk(s) for query: {}", docs.size(), query);
        return docs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));
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

    public List<ChatMessage> getConversationHistory(String sessionId) {
        return chatMemory.get(sessionId).stream()
                .map(this::toDomainMessage)
                .filter(msg -> msg != null && msg.getRole() != Role.SYSTEM)
                .collect(Collectors.toList());
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
