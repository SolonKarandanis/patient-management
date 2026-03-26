package com.pm.fts.web.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDocumentDTO extends AbstractDocumentDTO{

    protected String username;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String status;
    protected Boolean isEnabled;
    protected Boolean isVerified;
    protected List<String> rolesNames;
    protected List<Integer> roleIds;
}
