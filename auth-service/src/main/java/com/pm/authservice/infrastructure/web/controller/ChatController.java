package com.pm.authservice.infrastructure.web.controller;

import com.pm.authservice.infrastructure.ai.AiServiceClient;
import com.pm.authservice.infrastructure.web.dto.ChatRequestDTO;
import com.pm.authservice.infrastructure.web.dto.ChatResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final AiServiceClient aiServiceClient;

    @PostMapping
    public ResponseEntity<ChatResponseDTO> chat(@Valid @RequestBody ChatRequestDTO request) {
        return ResponseEntity.ok(aiServiceClient.chat(request));
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> clearSession(@PathVariable String sessionId) {
        aiServiceClient.clearSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}
