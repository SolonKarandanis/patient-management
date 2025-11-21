package com.pm.authservice.user.controller;

import com.pm.authservice.config.i18n.Translate;
import com.pm.authservice.user.dto.CreateUserDTO;
import com.pm.authservice.user.dto.UserDTO;
import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.user.model.UserEntity;
import com.pm.authservice.user.service.UserService;
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

    private final UserService usersService;

    public UserRegistrationController(UserService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/users")
    @Translate(path = "status", targetProperty = "statusLabel")
    public ResponseEntity<UserDTO> registerUser(@RequestBody @Valid CreateUserDTO user, final HttpServletRequest request)
            throws BusinessException {
        log.info("UserRegistrationController->registerUser");
        UserEntity userSaved=usersService.registerUser(user, applicationUrl(request));
        return ResponseEntity.ok(usersService.convertToDTO(userSaved,true));
    }


    protected String applicationUrl(HttpServletRequest request) {
        return "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
    }
}
