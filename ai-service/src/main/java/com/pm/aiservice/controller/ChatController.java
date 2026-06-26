package com.pm.aiservice.controller;

import com.pm.aiservice.dto.ChatRequest;
import com.pm.aiservice.dto.ChatResponse;
import com.pm.aiservice.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/ai/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public Mono<ResponseEntity<ChatResponse>> chat(@Valid @RequestBody ChatRequest request) {
        return Mono.fromCallable(() -> {
            String reply = chatService.chat(request.getSessionId(), request.getMessage());
            return ResponseEntity.ok(ChatResponse.builder()
                    .sessionId(request.getSessionId())
                    .response(reply)
                    .turnCount(chatService.getSessionTurnCount(request.getSessionId()))
                    .build());
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@Valid @RequestBody ChatRequest request) {
        return chatService.streamChat(request.getSessionId(), request.getMessage());
    }

    @DeleteMapping("/{sessionId}")
    public Mono<ResponseEntity<Void>> clearSession(@PathVariable String sessionId) {
        return Mono.<ResponseEntity<Void>>fromCallable(() -> {
            if (!chatService.sessionExists(sessionId)) {
                return ResponseEntity.notFound().<Void>build();
            }
            chatService.clearSession(sessionId);
            return ResponseEntity.noContent().<Void>build();
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
