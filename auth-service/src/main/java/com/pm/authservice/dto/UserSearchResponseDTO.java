package com.pm.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchResponseDTO {
    protected List<UserDocumentSearchResultsDTO> content;

    protected Long totalElements;
}
