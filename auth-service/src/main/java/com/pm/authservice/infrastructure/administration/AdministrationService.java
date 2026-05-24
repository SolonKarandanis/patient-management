package com.pm.authservice.infrastructure.administration;

import org.springframework.web.client.ResourceAccessException;

public interface AdministrationService {

    Boolean triggerAdHocIndexing();
    Boolean deleteUserIndex()  throws ResourceAccessException;
}
