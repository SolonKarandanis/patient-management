package com.pm.aiservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatResponse {

    private String sessionId;
    private String response;
    private int turnCount;
}
