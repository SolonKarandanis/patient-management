package com.pm.authservice.infrastructure.web.controller;

import com.pm.authservice.infrastructure.ai.AiServiceClient;
import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import com.pm.authservice.infrastructure.persistence.repository.UserJpaRepository;
import com.pm.authservice.infrastructure.web.dto.ChatHistoryResponseDTO;
import com.pm.authservice.infrastructure.web.dto.ChatRequestDTO;
import com.pm.authservice.infrastructure.web.dto.ChatResponseDTO;
import com.pm.authservice.infrastructure.web.dto.ChatServiceRequestDTO;
import com.pm.authservice.infrastructure.web.dto.UserDetailsDTO;
import com.pm.authservice.infrastructure.web.exception.NotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final AiServiceClient aiServiceClient;
    private final UserJpaRepository userRepository;

    @GetMapping("/history")
    public ResponseEntity<ChatHistoryResponseDTO> getHistory(Authentication authentication) {
        UserJpaEntity user = resolveCurrentUser(authentication);
        return ResponseEntity.ok(aiServiceClient.getHistory(user.getId().toString()));
    }

    @PostMapping
    public ResponseEntity<ChatResponseDTO> chat(@Valid @RequestBody ChatRequestDTO request, Authentication authentication) {
        UserJpaEntity user = resolveCurrentUser(authentication);
        ChatServiceRequestDTO serviceRequest = new ChatServiceRequestDTO(user.getId().toString(),request.getMessage());
        return ResponseEntity.ok(aiServiceClient.chat(serviceRequest));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearSession(Authentication authentication) {
        UserJpaEntity user = resolveCurrentUser(authentication);
        aiServiceClient.clearSession(user.getId().toString());
        return ResponseEntity.noContent().build();
    }

    private UserJpaEntity findUserByPublicId(String publicId) {
        return userRepository.findByDomainId(UUID.fromString(publicId))
                .orElseThrow(() -> new NotFoundException("error.user.not.found"));
    }

    private UserJpaEntity resolveCurrentUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("email");
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("error.user.not.found"));
        }
        UserDetailsDTO dto = (UserDetailsDTO) authentication.getPrincipal();
        assert dto != null;
        return findUserByPublicId(dto.getPublicId());
    }
}
