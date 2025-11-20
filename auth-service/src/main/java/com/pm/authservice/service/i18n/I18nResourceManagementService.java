package com.pm.authservice.service.i18n;

import com.pm.authservice.dto.I18nResourceManagementRequestDTO;
import com.pm.authservice.dto.I18nResourceManagementResponseDTO;
import com.pm.authservice.dto.SearchResults;
import org.springframework.data.domain.PageRequest;

public interface I18nResourceManagementService {
    SearchResults<I18nResourceManagementResponseDTO> searchI18nResources(I18nResourceManagementRequestDTO searchRequest, PageRequest pageRequest);
}
