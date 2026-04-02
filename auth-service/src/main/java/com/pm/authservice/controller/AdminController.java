package com.pm.authservice.controller;

import com.pm.authservice.util.AuthorityConstants;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RolesAllowed({AuthorityConstants.ROLE_SYSTEM_ADMIN})
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    @PostMapping("/indexing")
    public ResponseEntity<Void> triggerAdHocIndexing(){
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/indexing/user")
    public Boolean deleteUserIndex(){
        return null;
    }
}
