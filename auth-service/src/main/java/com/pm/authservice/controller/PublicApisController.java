package com.pm.authservice.controller;

import com.pm.authservice.dto.PublicConfiguration;
import com.pm.authservice.service.CommonEntitiesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class PublicApisController {

    private final CommonEntitiesService commonEntitiesService;

    public PublicApisController(CommonEntitiesService commonEntitiesService) {
        this.commonEntitiesService = commonEntitiesService;
    }

    @GetMapping("/public/config")
    public ResponseEntity<PublicConfiguration> getPublicApplicationConfig(){
        PublicConfiguration appConfig = commonEntitiesService.getPublicApplicationConfig();
        return ResponseEntity.ok(appConfig);
    }
}
