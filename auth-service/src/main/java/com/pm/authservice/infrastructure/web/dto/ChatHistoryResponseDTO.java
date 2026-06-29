package com.pm.authservice.infrastructure.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatHistoryResponseDTO {

    private String sessionId;
    private List<ChatMessageDTO> messages;

    @Data
    public static class ChatMessageDTO {
        private String role;
        private String content;
    }
}
