package com.pm.fts.repository.user;

import com.pm.fts.web.dto.DocumentSearchRequest;
import com.pm.fts.web.dto.SearchResponseDTO;

public interface UserDocumentCustomRepository <T extends SearchResponseDTO, P extends DocumentSearchRequest>{

    T quickSearch(P payload) throws Exception;

    T advancedSearch(P payload) throws Exception;

    Long countItems(P payload) throws Exception;

    T findActiveItems(P payload) throws Exception;
}
