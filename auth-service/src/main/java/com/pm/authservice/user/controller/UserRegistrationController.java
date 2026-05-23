package com.pm.authservice.user.controller;

import com.pm.authservice.infrastructure.i18n.config.Translate;
import com.pm.authservice.user.dto.CreateUserDTO;
import com.pm.authservice.user.dto.UserDTO;
import com.pm.authservice.user.service.UserRegistrationService;
import jakarta.servlet.http.HttpServletRequest;
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
public class UserRegistrationController {

    private static final Logger log = LoggerFactory.getLogger(UserRegistrationController.class);

    private final UserRegistrationService registrationService;

    public UserRegistrationController(UserRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/users")
    @Translate(path = "status", targetProperty = "statusLabel")
    public ResponseEntity<UserDTO> registerUser(@RequestBody @Valid CreateUserDTO user,
                                                final HttpServletRequest request) {
        log.info("UserRegistrationController->registerUser");
        UserDTO saved = registrationService.register(user, applicationUrl(request));
        return ResponseEntity.ok(saved);
    }

    protected String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
