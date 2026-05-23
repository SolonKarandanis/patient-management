package com.pm.authservice.domain.port.in;

import com.pm.authservice.domain.model.User;

import java.util.List;
import java.util.UUID;

public interface SearchUsersUseCase {

    record Query(
            String email,
            String username,
            String name,
            String status,
            String roleName,
            String searchMethod,
            int page,
            int size,
            String sortColumn,
            String sortDirection,
            UUID requestingUserDomainId
    ) {}

    record Result(List<User> users, long totalCount) {}

    Result search(Query query);
}
