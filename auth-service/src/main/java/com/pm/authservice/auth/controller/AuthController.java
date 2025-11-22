package com.pm.authservice.auth.controller;

import com.pm.authservice.auth.dto.JwtDTO;
import com.pm.authservice.auth.dto.LoginRequestDTO;
import com.pm.authservice.auth.UserDetailsDTO;
import com.pm.authservice.exception.AuthException;
import com.pm.authservice.auth.service.AuthService;
import com.pm.authservice.auth.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController( AuthService authService,JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Generate token on user login")
    @PostMapping("/login")
    public ResponseEntity<JwtDTO> authenticate(@Valid @RequestBody LoginRequestDTO submitCredentialsDTO)
            throws AuthException {
        log.info("AuthController->authenticate----------->email: {}",submitCredentialsDTO.getEmail());
        UserDetailsDTO authenticatedUser = authService.authenticate(submitCredentialsDTO);
        JwtDTO jwt = jwtService.generateToken(authenticatedUser);
        return ResponseEntity.ok().body(jwt);
    }

    @Operation(summary = "Validate Token")
    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(
            @RequestHeader("Authorization") String authHeader,
            Authentication authentication) throws AuthException {
        log.info("AuthController->validateToken");
        // Authorization: Bearer <token>
        if(!authHeader.startsWith("Bearer ")) {
            log.info("AuthController->validateToken-> Authentication header is invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetailsDTO dto = (UserDetailsDTO)authentication.getPrincipal();
        log.info("AuthController->validateToken-> username: {} ", dto.getUsername());
        return jwtService.isTokenValid(authHeader,dto)?
                ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
