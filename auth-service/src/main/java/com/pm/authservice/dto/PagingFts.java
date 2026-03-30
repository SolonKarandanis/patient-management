package com.pm.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PagingFts {
    private int page;
    private int limit;
    private List<String> sortFields;
    private String sortDirection;
}
