package com.pm.aiservice.controller;

import com.pm.aiservice.dto.ChatRequest;
import com.pm.aiservice.dto.ChatResponse;
import com.pm.aiservice.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        String reply = chatService.chat(request.getSessionId(), request.getMessage());
        return ResponseEntity.ok(ChatResponse.builder()
                .sessionId(request.getSessionId())
                .response(reply)
                .turnCount(chatService.getSessionTurnCount(request.getSessionId()))
                .build());
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> clearSession(@PathVariable String sessionId) {
        if (!chatService.sessionExists(sessionId)) {
            return ResponseEntity.notFound().build();
        }
        chatService.clearSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}
