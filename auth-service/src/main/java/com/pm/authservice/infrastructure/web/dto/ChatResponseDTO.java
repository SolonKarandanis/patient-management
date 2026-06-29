package com.pm.authservice.infrastructure.web.dto;

import lombok.Data;

@Data
public class ChatResponseDTO {

    private String sessionId;
    private String response;
    private int turnCount;
}
