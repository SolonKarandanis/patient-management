package com.pm.fts.web.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public class UserDocumentSearchResultsDTO {

    protected String publicId;
    protected String username;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String status;
    protected Boolean isEnabled;
    protected List<Integer> roleIds;
}
