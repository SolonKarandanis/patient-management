package com.pm.authservice.infrastructure.web;

import com.pm.authservice.infrastructure.web.dto.ApplicationConfigDTO;
import com.pm.authservice.infrastructure.web.dto.PublicConfiguration;

public interface CommonEntitiesService {

    ApplicationConfigDTO getApplicationConfig();

    PublicConfiguration getPublicApplicationConfig();
}
