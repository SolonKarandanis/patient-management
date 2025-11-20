package com.pm.authservice.service;

import com.pm.authservice.dto.Paging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service("searchService")
@Transactional(propagation = Propagation.SUPPORTS)
@Slf4j
public class SearchServiceBean extends GenericServiceBean implements SearchService{

    @Override
    public Sort getPageSort(Paging paging) {
        return getPageSort(paging, null);
    }

    @Override
    public Sort getPageSort(Paging paging, List<Sort.Order> secondaryOrdering) {
        Sort sort = Sort.by(Sort.Order.asc("id"));

        if (paging == null || !StringUtils.hasLength(paging.getSortingColumn())) {
            return sort;
        }

        Sort.Order primaryOrdering = null;
        if (StringUtils.hasLength(paging.getSortingDirection())
                && paging.getSortingDirection().equalsIgnoreCase("DESC")) {
            primaryOrdering = Sort.Order.desc(paging.getSortingColumn());
        } else {
            primaryOrdering = Sort.Order.asc(paging.getSortingColumn());
        }
        Sort.Order[] ordering = null;
        if (secondaryOrdering != null && !secondaryOrdering.isEmpty()) {
            List<Sort.Order> orderList = new ArrayList<>();
            orderList.add(primaryOrdering);
            orderList.addAll(secondaryOrdering);
            ordering = orderList.toArray(new Sort.Order[0]);
        } else {
            ordering = new Sort.Order[] { primaryOrdering };
        }
        sort = Sort.by(ordering);

        return sort;
    }

    @Override
    public PageRequest toPageRequest(Paging paging) {
        return toPageRequest(paging, null);
    }

    @Override
    public PageRequest toPageRequest(Paging paging, List<Sort.Order> secondaryOrdering) {
        PageRequest output = null;

        Sort sort = getPageSort(paging, secondaryOrdering);
        if (paging != null) {
            Integer size = paging.getPagingSize();
            Integer pageNo = paging.getPagingStart();
            output = PageRequest.of(pageNo, size, sort);
        } else {
            output = PageRequest.of(Paging.DEFAULT_PAGE_START, Paging.DEFAULT_PAGE_SIZE, sort);
        }
        return output;
    }

    @Override
    public PageRequest toPageRequest(Integer page, Integer size, String sortOrder, String sortField) {
        return toPageRequest(page, size, sortOrder, sortField, null);
    }

    @Override
    public PageRequest toPageRequest(Integer page, Integer size, String sortOrder, String sortField, List<Sort.Order> secondaryOrdering) {
        Paging paging = new Paging();
        if (size != null) {
            paging.setPagingSize(size);
        }
        if (page != null) {
            paging.setPagingStart(page);
        }
        Integer pageNo = paging.getPagingStart();

        paging.setSortingDirection(sortOrder);
        paging.setSortingColumn(sortField);
        Sort sort = getPageSort(paging, secondaryOrdering);
        return PageRequest.of(pageNo, size, sort);
    }
}
