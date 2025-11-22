package com.pm.authservice.user.controller;

import com.pm.authservice.config.Translate;
import com.pm.authservice.dto.RoleDTO;
import com.pm.authservice.user.model.RoleEntity;
import com.pm.authservice.user.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }


    @GetMapping
    @Translate(path = "[*].name", targetProperty = "nameLabel")
    public ResponseEntity<List<RoleDTO>> findAllRoles(){
        Set<RoleEntity> roles = new HashSet<>(roleService.findAll());
        List<RoleDTO> dtos = roleService.convertToDtoList(roles);
        return ResponseEntity.ok(dtos);
    }
}
