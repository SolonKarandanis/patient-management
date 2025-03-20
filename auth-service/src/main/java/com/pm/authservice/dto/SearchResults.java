package com.pm.authservice.dto;

import java.util.List;

public class SearchResults<T> {
	
	private List<T> list;
    private int countRows;

    public SearchResults(int countRows, List<T> list) {
        this.countRows = countRows;
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getCountRows() {
        return countRows;
    }

    public void setCountRows(int countRows) {
        this.countRows = countRows;
    }


}
