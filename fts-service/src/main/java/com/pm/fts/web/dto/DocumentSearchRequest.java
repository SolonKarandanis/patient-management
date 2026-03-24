package com.pm.fts.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class DocumentSearchRequest {
    public enum Type {
        QUICK,
        ADVANCED
    }

    public enum Status {
        ACTIVE,
        ALL
    }

    public enum Role {
        SA,
        DOCTOR,
        PATIENT,
        GUEST
    }

    @Valid
    @NotNull
    private Type type;

    @Valid
    private Paging paging;

    @NotNull
    private Status status;

    @NotNull
    private Role role;

    @Singular("criterion")
    @Valid
    private List<SearchCriterion> criteria;

    private List<String> resultFields;
}
