package com.pm.aiservice.domain.port;

import com.pm.aiservice.domain.model.ChatMessage;
import reactor.core.publisher.Flux;

import java.util.List;

public interface LlmPort {
    String chat(List<ChatMessage> history);
    Flux<String> streamChat(List<ChatMessage> history);
}
