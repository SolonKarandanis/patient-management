package com.pm.aiservice.infrastructure.memory;

import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
public class JdbcChatMemoryRepository implements ChatMemoryRepository {

    private final JdbcTemplate jdbc;

    public JdbcChatMemoryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<String> findConversationIds() {
        return jdbc.queryForList(
                "SELECT DISTINCT conversation_id FROM spring_ai_chat_memory ORDER BY conversation_id",
                String.class);
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        return jdbc.query(
                "SELECT content, type FROM spring_ai_chat_memory WHERE conversation_id = ? ORDER BY timestamp ASC",
                (rs, rowNum) -> toMessage(rs.getString("content"), rs.getString("type")),
                conversationId);
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        jdbc.update("DELETE FROM spring_ai_chat_memory WHERE conversation_id = ?", conversationId);
        if (messages.isEmpty()) return;
        jdbc.batchUpdate(
                "INSERT INTO spring_ai_chat_memory (conversation_id, content, type, timestamp) VALUES (?, ?, ?, ?)",
                messages,
                messages.size(),
                (ps, msg) -> {
                    ps.setString(1, conversationId);
                    ps.setString(2, msg.getText());
                    ps.setString(3, msg.getMessageType().name());
                    ps.setTimestamp(4, Timestamp.from(Instant.now()));
                });
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        jdbc.update("DELETE FROM spring_ai_chat_memory WHERE conversation_id = ?", conversationId);
    }

    private Message toMessage(String content, String type) {
        return switch (MessageType.valueOf(type)) {
            case USER -> new UserMessage(content);
            case ASSISTANT -> new AssistantMessage(content);
            case SYSTEM -> new SystemMessage(content);
            default -> throw new IllegalArgumentException("Unsupported message type: " + type);
        };
    }
}
