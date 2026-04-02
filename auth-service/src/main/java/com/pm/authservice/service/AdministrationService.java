package com.pm.authservice.service;

import org.springframework.web.client.ResourceAccessException;

public interface AdministrationService {

    Boolean triggerAdHocIndexing();
    Boolean deleteUserIndex()  throws ResourceAccessException;
}
