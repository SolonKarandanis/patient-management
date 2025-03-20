package com.pm.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractPaging {
	
	@JsonProperty("page")
    protected Integer pagingStart;

    @JsonProperty("limit")
    protected Integer pagingSize;

    @JsonProperty("sortField")
    protected String sortingColumn;

    @JsonProperty("sortOrder")
    protected String sortingDirection; 

}
