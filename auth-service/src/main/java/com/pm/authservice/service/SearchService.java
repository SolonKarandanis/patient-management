package com.pm.authservice.service;

import com.pm.authservice.dto.Paging;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface SearchService {

    Sort getPageSort(Paging paging);

    Sort getPageSort(Paging paging, List<Sort.Order> secondaryOrdering);

    PageRequest toPageRequest(Paging paging);

    PageRequest toPageRequest(Paging paging, List<Sort.Order> secondaryOrdering);

    PageRequest toPageRequest(Integer page, Integer size, String sortOrder, String sortField);

    PageRequest toPageRequest(Integer page, Integer size, String sortOrder, String sortField, List<Sort.Order> secondaryOrdering);
}
