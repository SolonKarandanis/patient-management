package com.pm.notificationservice.shared.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class SearchResults<T> {
	
	private List<T> list;
    private long countRows;

    public SearchResults(long countRows, List<T> list) {
        this.countRows = countRows;
        this.list = list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public void setCountRows(long countRows) {
        this.countRows = countRows;
    }


}
