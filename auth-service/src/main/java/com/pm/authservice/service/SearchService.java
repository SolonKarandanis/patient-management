package com.pm.authservice.service;

import com.pm.authservice.dto.Paging;
import com.pm.authservice.dto.SearchResults;
import com.pm.authservice.dto.UserDocumentSearchResultsDTO;
import com.pm.authservice.exception.AuthException;
import com.pm.authservice.user.dto.UsersSearchRequestDTO;
import com.pm.authservice.user.model.UserEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

public interface SearchService {

    Sort getPageSort(Paging paging);

    Sort getPageSort(Paging paging, List<Sort.Order> secondaryOrdering);

    PageRequest toPageRequest(Paging paging);

    PageRequest toPageRequest(Paging paging, List<Sort.Order> secondaryOrdering);

    PageRequest toPageRequest(Integer page, Integer size, String sortOrder, String sortField);

    PageRequest toPageRequest(Integer page, Integer size, String sortOrder, String sortField, List<Sort.Order> secondaryOrdering);

    SearchResults<UserDocumentSearchResultsDTO> advancedSearchUsers(UsersSearchRequestDTO request,
                                                                    UserEntity loggedUser) throws ResourceAccessException, AuthException;

    Long countItems(UsersSearchRequestDTO request, UserEntity loggedUser) throws ResourceAccessException, AuthException;

    List<UserDocumentSearchResultsDTO> findUsersForExport(UsersSearchRequestDTO searchRequest, UserEntity user)
            throws ResourceAccessException, AuthException;

    SearchResults<UserDocumentSearchResultsDTO> quickSearchUsers(String quickSearchValueParam, UserEntity loggedUser,
                                                                     Integer page, Integer size, String sortField, String sortOrder) throws ResourceAccessException, AuthException;
}
