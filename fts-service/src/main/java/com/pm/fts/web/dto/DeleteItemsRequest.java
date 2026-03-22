package com.pm.fts.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeleteItemsRequest {
    private List<Integer> itemIds;
}
