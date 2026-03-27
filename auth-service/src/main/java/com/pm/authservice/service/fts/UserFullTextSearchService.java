package com.pm.authservice.service.fts;

import com.pm.authservice.dto.DocumentSearchRequest;
import com.pm.authservice.dto.UserDocumentDTO;
import com.pm.authservice.dto.UserDocumentSearchResultsDTO;
import com.pm.authservice.dto.UserSearchResponseDTO;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

public interface UserFullTextSearchService {

    UserSearchResponseDTO searchUsers(DocumentSearchRequest payload) throws ResourceAccessException;

    Long countUsers(DocumentSearchRequest payload) throws ResourceAccessException;

    List<UserDocumentSearchResultsDTO> findUsers(DocumentSearchRequest payload) throws ResourceAccessException;

    Boolean indexUsers(List<UserDocumentDTO> documents) throws ResourceAccessException;

    Boolean deleteUsersByIds(List<Integer> itemIds) throws ResourceAccessException;

    Boolean deleteUserIndex() throws ResourceAccessException;
}
