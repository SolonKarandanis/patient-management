package com.pm.aiservice.mcp;

import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AiServiceMcpTools {

    private final VectorStore vectorStore;
    private final ChatMemoryRepository chatMemoryRepository;

    public AiServiceMcpTools(VectorStore vectorStore, ChatMemoryRepository chatMemoryRepository) {
        this.vectorStore = vectorStore;
        this.chatMemoryRepository = chatMemoryRepository;
    }

    @Tool(description = "Search indexed clinical documents and knowledge base in pgvector store")
    public List<String> searchClinicalKnowledge(String query) {
        return vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(3).build()
        ).stream()
         .map(Document::getText)
         .collect(Collectors.toList());
    }

    @Tool(description = "List all active chat support conversation session IDs")
    public List<String> listChatSessions() {
        return chatMemoryRepository.findConversationIds();
    }

    @Tool(description = "Get a formatted, chronological transcript of a specific chat support session by its ID")
    public String getChatSessionTranscript(String sessionId) {
        List<Message> messages = chatMemoryRepository.findByConversationId(sessionId);
        if (messages.isEmpty()) {
            return "No messages found for session: " + sessionId;
        }

        return messages.stream()
                .map(msg -> "[" + msg.getMessageType() + "]: " + msg.getText())
                .collect(Collectors.joining("\n\n"));
    }
}
