package com.pm.authservice.infrastructure.ai;

import com.pm.authservice.infrastructure.web.dto.ChatRequestDTO;
import com.pm.authservice.infrastructure.web.dto.ChatResponseDTO;

public interface AiServiceClient {

    ChatResponseDTO chat(ChatRequestDTO request);

    void clearSession(String sessionId);

}
