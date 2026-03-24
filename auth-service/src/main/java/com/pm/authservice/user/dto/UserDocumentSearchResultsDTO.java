package com.pm.authservice.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
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
