package com.pm.authservice.infrastructure.config;

import com.pm.authservice.domain.annotation.DomainService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
        basePackages = "com.pm.authservice.domain",
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = DomainService.class
        ),
        useDefaultFilters = false
)
public class DomainConfig {
}
