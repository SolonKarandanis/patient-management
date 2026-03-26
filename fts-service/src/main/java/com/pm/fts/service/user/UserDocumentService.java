package com.pm.fts.service.user;

import com.pm.fts.document.user.UserDocument;
import com.pm.fts.web.dto.DocumentSearchRequest;
import com.pm.fts.web.dto.UserDocumentDTO;
import com.pm.fts.web.dto.UserDocumentSearchResultsDTO;
import com.pm.fts.web.dto.UserSearchResponseDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface UserDocumentService {

    void saveAllUsers(List<UserDocument> items);

    UserDocument convertToDocument(UserDocumentDTO dto);

    List<UserDocument> convertToDocuments(List<UserDocumentDTO> dtoList);

    UserSearchResponseDTO search(@Valid DocumentSearchRequest payload) throws Exception;

    Long countItems(@Valid DocumentSearchRequest payload) throws Exception;

    List<UserDocumentSearchResultsDTO> findDocumentUsers(@Valid DocumentSearchRequest payload) throws Exception;
}
