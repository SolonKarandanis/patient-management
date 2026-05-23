package com.pm.authservice.auth.controller;

import com.pm.authservice.auth.dto.JwtDTO;
import com.pm.authservice.auth.dto.LoginRequestDTO;
import com.pm.authservice.auth.dto.UserDetailsDTO;
import com.pm.authservice.domain.exception.BusinessRuleException;
import com.pm.authservice.domain.exception.UserNotFoundException;
import com.pm.authservice.domain.model.User;
import com.pm.authservice.domain.port.in.AuthenticateUserUseCase;
import com.pm.authservice.exception.AuthException;
import com.pm.authservice.auth.service.JwtService;
import com.pm.authservice.user.dto.RoleDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final JwtService jwtService;

    public AuthController(AuthenticateUserUseCase authenticateUserUseCase, JwtService jwtService) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Generate token on user login")
    @PostMapping("/login")
    public ResponseEntity<JwtDTO> authenticate(@Valid @RequestBody LoginRequestDTO submitCredentialsDTO)
            throws AuthException {
        log.info("AuthController->authenticate->email: {}", submitCredentialsDTO.getEmail());
        try {
            User domainUser = authenticateUserUseCase.authenticate(
                    submitCredentialsDTO.getEmail(), submitCredentialsDTO.getPassword());
            UserDetailsDTO userDetailsDTO = toUserDetailsDTO(domainUser);
            JwtDTO jwt = jwtService.generateToken(userDetailsDTO);
            return ResponseEntity.ok().body(jwt);
        } catch (UserNotFoundException | BusinessRuleException e) {
            throw new AuthException("error.invalid.credentials");
        }
    }

    @Operation(summary = "Validate Token")
    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(
            @RequestHeader("Authorization") String authHeader,
            Authentication authentication) throws AuthException {
        log.info("AuthController->validateToken");
        if (!authHeader.startsWith("Bearer ")) {
            log.info("AuthController->validateToken->Authentication header is invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetails dto = (UserDetails) authentication.getPrincipal();
        log.info("AuthController->validateToken->username: {}", dto.getUsername());
        return jwtService.isTokenValid(authHeader, dto) ?
                ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private UserDetailsDTO toUserDetailsDTO(User user) {
        UserDetailsDTO dto = new UserDetailsDTO(
                user.getDomainId().toString(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail()
        );
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setIsEnabled(user.getIsEnabled());
        dto.setStatus(user.getStatus());
        if (user.getRoles() != null) {
            List<RoleDTO> roles = user.getRoles().stream()
                    .map(r -> new RoleDTO(r.getId(), r.getName()))
                    .toList();
            dto.setRoleEntities(roles);
        }
        return dto;
    }
}
