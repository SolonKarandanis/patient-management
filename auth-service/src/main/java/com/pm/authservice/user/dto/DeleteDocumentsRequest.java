package com.pm.authservice.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeleteDocumentsRequest {
    private List<Integer> documentIds;
}
