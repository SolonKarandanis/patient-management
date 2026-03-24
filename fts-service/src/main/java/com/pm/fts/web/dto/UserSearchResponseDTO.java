package com.pm.fts.web.dto;

import com.pm.fts.document.user.UserDocument;
import org.springframework.data.elasticsearch.core.SearchHits;

public class UserSearchResponseDTO extends SearchResponseDTO<UserDocumentSearchResultsDTO, UserDocument>{

    public UserSearchResponseDTO(SearchHits<UserDocument> hits, long count) {
        super(hits, count);
    }

    @Override
    protected UserDocumentSearchResultsDTO toDTO(UserDocument document) {
        return UserDocumentSearchResultsDTO.builder().publicId(document.getPublicId())
                .firstName(document.getFirstName()).lastName(document.getLastName()).email(document.getEmail())
                .isEnabled(document.getIsEnabled()).status(document.getStatus()).username(document.getUsername())
                .roleIds(document.getRoleIds()).build();
    }
}
