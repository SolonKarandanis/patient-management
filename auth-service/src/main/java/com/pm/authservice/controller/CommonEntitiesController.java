package com.pm.authservice.controller;

import com.pm.authservice.dto.ApplicationConfigDTO;
import com.pm.authservice.service.CommonEntitiesService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/common")
public class CommonEntitiesController {

    private final CommonEntitiesService commonEntitiesService;

    public CommonEntitiesController(CommonEntitiesService commonEntitiesService) {
        this.commonEntitiesService = commonEntitiesService;
    }

    @GetMapping("/config")
    public ResponseEntity<ApplicationConfigDTO> getAppConfig() {
        ApplicationConfigDTO appConfig = commonEntitiesService.getApplicationConfig();
        return ResponseEntity.ok(appConfig);
    }


}
