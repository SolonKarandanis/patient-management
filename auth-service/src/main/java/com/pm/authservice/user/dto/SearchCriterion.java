package com.pm.authservice.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SearchCriterion {

    public enum Type {
        TEXT,
        DATE,
        INTEGER,
        LONG,
        DOUBLE,
        BOOLEAN
    }

    public enum SearchType {
        TERM,
        WILDCARD,
        RANGE_EXCLUSIVE,
        RANGE_INCLUSIVE,
        COMPLEX,
        MATCH,
        MATCH_PREFIX,
        MATCH_PREFIX_OR_WILDCARD,
        NESTED
    } //MATCH, RANGE_EXCLUSIVE, RANGE_INCLUSIVE

    public enum FTSOperation {
        OR,
        AND,
        NOT
    }

    /**
     * the name of the search field
     */
    @NotBlank
    private String field;

    /**
     * if it is a date, the format pattern
     */
    private String format;

    /**
     * list of values for the search field
     */
    @Singular
    private List<? extends Object> values;
    /**
     * the data type of the search field
     */
    @NotNull
    private Type type;
    /**
     * the type of search to perform (ex. MATCH vs TERM)
     */
    @NotNull
    private SearchType searchType;

    private String from;

    private String to;

    private FTSOperation operation;

    public String getKeywordField() {
        return field + ".keyword";
    }

    private Boolean isNotNull;
}
