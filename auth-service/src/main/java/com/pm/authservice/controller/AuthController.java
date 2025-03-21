package com.pm.authservice.controller;

import com.pm.authservice.dto.JwtDTO;
import com.pm.authservice.dto.LoginRequestDTO;
import com.pm.authservice.dto.UserDetailsDTO;
import com.pm.authservice.exception.AuthException;
import com.pm.authservice.service.AuthService;
import com.pm.authservice.service.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController( AuthService authService,JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JwtDTO> authenticate(@Valid @RequestBody LoginRequestDTO submitCredentialsDTO)
            throws AuthException {
        log.info("AuthController->authenticate----------->username: {}   password: {}",submitCredentialsDTO.getEmail(),submitCredentialsDTO.getPassword());
        UserDetailsDTO authenticatedUser = authService.authenticate(submitCredentialsDTO);
        JwtDTO jwt = jwtService.generateToken(authenticatedUser);
        return ResponseEntity.ok().body(jwt);
    }
}
