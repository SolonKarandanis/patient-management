package com.pm.authservice.controller;

import com.pm.authservice.dto.JwtDTO;
import com.pm.authservice.dto.LoginRequestDTO;
import com.pm.authservice.dto.UserDetailsDTO;
import com.pm.authservice.exception.AuthException;
import com.pm.authservice.service.AuthService;
import com.pm.authservice.service.JwtService;
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
        log.info("AuthController->authenticate----------->username: {}   password: {}",submitCredentialsDTO.getEmail(),submitCredentialsDTO.getPassword());
        UserDetailsDTO authenticatedUser = authService.authenticate(submitCredentialsDTO);
        JwtDTO jwt = jwtService.generateToken(authenticatedUser);
        return ResponseEntity.ok().body(jwt);
    }

    @Operation(summary = "Validate Token")
    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(
            @RequestHeader("Authorization") String authHeader,
            Authentication authentication) throws AuthException {
        // Authorization: Bearer <token>
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetailsDTO dto = (UserDetailsDTO)authentication.getPrincipal();
        return jwtService.isTokenValid(authHeader,dto)?
                ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
