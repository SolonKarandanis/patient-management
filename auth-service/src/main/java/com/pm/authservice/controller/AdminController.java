package com.pm.authservice.controller;

import com.pm.authservice.service.AdministrationService;
import com.pm.authservice.util.AuthorityConstants;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

    private final AdministrationService administrationService;

    @Value("${search.elasticSearch.enable:false}")
    private Boolean elasticSearchEnable;

    public AdminController(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @PostMapping("/indexing")
    public ResponseEntity<Void> triggerAdHocIndexing(){
        if(!elasticSearchEnable){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/indexing/user")
    public ResponseEntity<Void> deleteUserIndex(){
        if(!elasticSearchEnable){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Boolean result = administrationService.deleteUserIndex();
        if(result){
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
