package com.pm.aiservice.domain.port;

import reactor.core.publisher.Flux;

public interface LlmPort {
    String chat(String sessionId, String userMessage);
    Flux<String> streamChat(String sessionId, String userMessage);
}
