package com.pm.authservice.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatServiceRequestDTO {

    @NotBlank
    private String sessionId;

    @NotBlank
    private String message;
}
