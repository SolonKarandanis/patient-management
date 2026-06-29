package com.pm.authservice.infrastructure.ai;

import com.pm.authservice.infrastructure.web.dto.ChatHistoryResponseDTO;
import com.pm.authservice.infrastructure.web.dto.ChatResponseDTO;
import com.pm.authservice.infrastructure.web.dto.ChatServiceRequestDTO;

public interface AiServiceClient {

    ChatResponseDTO chat(ChatServiceRequestDTO request);

    ChatHistoryResponseDTO getHistory(String sessionId);

    void clearSession(String sessionId);

}
