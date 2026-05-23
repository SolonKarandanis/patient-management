package com.pm.authservice.domain.model;

public record UserSearchCriteria(
        String email,
        String username,
        String name,
        String status,
        String roleName,
        String searchMethod,
        int page,
        int size,
        String sortColumn,
        String sortDirection
) {}
