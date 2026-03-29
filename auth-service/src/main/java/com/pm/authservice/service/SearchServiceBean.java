package com.pm.authservice.service;

import com.pm.authservice.dto.DocumentSearchRequest;
import com.pm.authservice.dto.Paging;
import com.pm.authservice.dto.SearchResults;
import com.pm.authservice.dto.UserDocumentSearchResultsDTO;
import com.pm.authservice.exception.AuthException;
import com.pm.authservice.service.fts.FtsUtil;
import com.pm.authservice.service.fts.UserFullTextSearchService;
import com.pm.authservice.user.dto.UsersSearchRequestDTO;
import com.pm.authservice.user.model.UserEntity;
import com.pm.authservice.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.List;

@Service("searchService")
@Transactional(propagation = Propagation.SUPPORTS)
@Slf4j
public class SearchServiceBean  implements SearchService{

    private final UserService userService;
    private final UserFullTextSearchService userFullTextSearchService;

    @Value("${search.elasticSearch.enable:false}")
    private Boolean elasticSearchEnable;

    public SearchServiceBean(UserService userService, UserFullTextSearchService userFullTextSearchService) {
        this.userService = userService;
        this.userFullTextSearchService = userFullTextSearchService;
    }

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

    protected void logIfElasticSearchIsEnabled(){
        log.debug("isElasticSearchEnabled: {}", elasticSearchEnable);
    }

    protected void checkRequestParamsValidity(String status, String searchMethod) throws AuthException {
        boolean isPermittedItemStatusValue = FtsUtil.getPermittedSearchUsersStatusValues().stream()
                .anyMatch(s -> s.equals(status));
        boolean isPermittedItemSearchMethodValue = FtsUtil.getPermittedSearchUsersSearchMethodValues().stream()
                .anyMatch(s -> s.equals(searchMethod));
        String[] arguments = null;
        if (!isPermittedItemStatusValue) {
            arguments = FtsUtil.getPermittedSearchUsersStatusValues().toArray(new String[0]);
            throw new AuthException("error.search.user.status", arguments);
        }
        if (!isPermittedItemSearchMethodValue) {
            arguments = FtsUtil.getPermittedSearchUsersSearchMethodValues().toArray(new String[0]);
            throw new AuthException("error.search.user.method", arguments);
        }
    }

    @Override
    public SearchResults<UserDocumentSearchResultsDTO> advancedSearchUsers(UsersSearchRequestDTO request, UserEntity loggedUser)
            throws ResourceAccessException, AuthException {
        log.debug("in SearchServiceBean ----> advancedSearchUsers");
        logIfElasticSearchIsEnabled();
        String status = request.getStatus();
//        String searchMethod = request.getSearchMethod();
        return null;
    }

    @Override
    public Long countItems(UsersSearchRequestDTO request, UserEntity loggedUser) throws ResourceAccessException, AuthException {
        log.debug("in SearchServiceBean ----> countItems");
        logIfElasticSearchIsEnabled();
        return 0L;
    }

    @Override
    public List<UserDocumentSearchResultsDTO> findUsersForExport(UsersSearchRequestDTO searchRequest, UserEntity user)
            throws ResourceAccessException, AuthException {
        log.debug("in SearchServiceBean ----> findUsersForExport");
        logIfElasticSearchIsEnabled();
        return List.of();
    }

    @Override
    public SearchResults<UserDocumentSearchResultsDTO> quickSearchUsers(String quickSearchValueParam, UserEntity loggedUser,
                                                                        Integer page, Integer size, String sortField, String sortOrder)
            throws ResourceAccessException, AuthException {
        log.debug("in SearchServiceBean ----> quickSearchUsers");
        logIfElasticSearchIsEnabled();
        return null;
    }
}
