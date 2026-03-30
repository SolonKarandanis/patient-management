package com.pm.authservice.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter 
public class SearchRequestDTO {

	protected String searchMethod;
	
	protected Paging paging;
}
