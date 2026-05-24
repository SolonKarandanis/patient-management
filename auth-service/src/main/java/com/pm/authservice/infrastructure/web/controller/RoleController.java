package com.pm.authservice.infrastructure.web.controller;

import com.pm.authservice.infrastructure.config.i18n.Translate;
import com.pm.authservice.infrastructure.web.dto.RoleDTO;
import com.pm.authservice.infrastructure.application.RoleQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleQueryService roleQueryService;

    public RoleController(RoleQueryService roleQueryService) {
        this.roleQueryService = roleQueryService;
    }

    @GetMapping
    @Translate(path = "[*].name", targetProperty = "nameLabel")
    public ResponseEntity<List<RoleDTO>> findAllRoles(){
        return ResponseEntity.ok(roleQueryService.findAllRoles());
    }
}
