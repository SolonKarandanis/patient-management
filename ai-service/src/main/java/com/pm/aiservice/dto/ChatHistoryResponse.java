package com.pm.aiservice.dto;

import com.pm.aiservice.domain.model.ChatMessage;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatHistoryResponse {
    private String sessionId;
    private List<ChatMessage> messages;
}
