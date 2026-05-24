package com.pm.authservice.infrastructure.application;

import com.pm.authservice.infrastructure.web.dto.I18nResourceManagementRequestDTO;
import com.pm.authservice.infrastructure.web.dto.I18nResourceManagementResponseDTO;
import com.pm.authservice.infrastructure.web.dto.SearchResults;
import org.springframework.data.domain.PageRequest;

public interface I18nResourceManagementService {
    SearchResults<I18nResourceManagementResponseDTO> searchI18nResources(I18nResourceManagementRequestDTO searchRequest, PageRequest pageRequest);
}
