package com.pm.authservice.infrastructure.search;

import com.pm.authservice.infrastructure.search.dto.DocumentSearchRequest;
import com.pm.authservice.infrastructure.search.dto.UserDocumentDTO;
import com.pm.authservice.infrastructure.search.dto.UserDocumentSearchResultsDTO;
import com.pm.authservice.infrastructure.search.dto.UserSearchResponseDTO;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

public interface UserFullTextSearchClient {

    UserSearchResponseDTO searchUsers(DocumentSearchRequest payload) throws ResourceAccessException;

    Long countUsers(DocumentSearchRequest payload) throws ResourceAccessException;

    List<UserDocumentSearchResultsDTO> findUsers(DocumentSearchRequest payload) throws ResourceAccessException;

    Boolean indexUsers(List<UserDocumentDTO> documents) throws ResourceAccessException;

    Boolean deleteUsersByIds(List<Integer> itemIds) throws ResourceAccessException;

    Boolean deleteUserIndex() throws ResourceAccessException;
}
