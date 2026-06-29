package com.pm.authservice.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequestDTO {

    @NotBlank
    private String sessionId;

    @NotBlank
    private String message;
}
