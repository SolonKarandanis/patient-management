package com.pm.authservice.infrastructure.ai;

import com.pm.authservice.infrastructure.web.dto.ChatResponseDTO;
import com.pm.authservice.infrastructure.web.dto.ChatServiceRequestDTO;

public interface AiServiceClient {

    ChatResponseDTO chat(ChatServiceRequestDTO request);

    void clearSession(String sessionId);

}
