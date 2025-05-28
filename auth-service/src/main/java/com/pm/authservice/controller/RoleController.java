package com.pm.authservice.controller;

import com.pm.authservice.dto.RoleDTO;
import com.pm.authservice.model.Role;
import com.pm.authservice.service.RoleService;
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
    public ResponseEntity<List<RoleDTO>> findAllRoles(){
        Set<Role> roles = new HashSet<>(roleService.findAll());
        List<RoleDTO> dtos = roleService.convertToDtoList(roles);
        return ResponseEntity.ok(dtos);
    }
}
