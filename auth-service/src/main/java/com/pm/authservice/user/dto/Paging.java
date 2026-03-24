package com.pm.authservice.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Paging {
    private int page;
    private int limit;
    private List<String> sortFields;
    private String sortDirection;
}
