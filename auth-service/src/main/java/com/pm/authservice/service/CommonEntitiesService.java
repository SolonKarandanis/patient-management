package com.pm.authservice.service;

import com.pm.authservice.dto.ApplicationConfigDTO;
import com.pm.authservice.dto.PublicConfiguration;

public interface CommonEntitiesService {

    ApplicationConfigDTO getApplicationConfig();

    PublicConfiguration getPublicApplicationConfig();
}
