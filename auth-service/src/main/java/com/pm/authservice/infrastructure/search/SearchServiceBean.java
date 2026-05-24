package com.pm.authservice.infrastructure.search;

import com.pm.authservice.infrastructure.search.dto.*;
import com.pm.authservice.infrastructure.web.dto.Paging;
import com.pm.authservice.infrastructure.web.dto.RoleDTO;
import com.pm.authservice.infrastructure.web.dto.SearchResults;
import com.pm.authservice.infrastructure.web.dto.UserDTO;
import com.pm.authservice.infrastructure.web.dto.UsersSearchRequestDTO;
import com.pm.authservice.infrastructure.web.exception.AuthException;
import com.pm.authservice.domain.model.AccountStatus;
import com.pm.authservice.domain.model.AuthorityConstants;
import com.pm.authservice.infrastructure.persistence.entity.QUserJpaEntity;
import com.pm.authservice.infrastructure.persistence.entity.RoleJpaEntity;
import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import com.pm.authservice.infrastructure.persistence.repository.RoleJpaRepository;
import com.pm.authservice.infrastructure.persistence.repository.UserJpaRepository;
import com.pm.authservice.infrastructure.util.AppConstants;
import com.pm.authservice.infrastructure.util.UserUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;

import java.util.*;
import java.util.stream.StreamSupport;

@Service("searchService")
@Transactional(propagation = Propagation.SUPPORTS)
@Slf4j
public class SearchServiceBean implements SearchService {

    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;
    private final UserFullTextSearchService userFullTextSearchService;

    @Value("${search.elasticSearch.enable:false}")
    private Boolean elasticSearchEnable;

    public SearchServiceBean(UserJpaRepository userRepository,
                             RoleJpaRepository roleRepository,
                             UserFullTextSearchService userFullTextSearchService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userFullTextSearchService = userFullTextSearchService;
    }

    // -----------------------------------------------------------------------
    // Absorbed user search helpers (previously in UserServiceBean)
    // -----------------------------------------------------------------------

    private Page<UserJpaEntity> searchUsers(UsersSearchRequestDTO searchObj, UserJpaEntity loggedUser) {
        PageRequest pageRequest = toPageRequestFromPaging(searchObj.getPaging());
        Predicate predicate = getSearchPredicate(searchObj, loggedUser);
        return userRepository.findAll(predicate, pageRequest);
    }

    private Page<UserJpaEntity> quickSearchUsersDb(String quickSearchValueParam, PageRequest pageRequest, UserJpaEntity loggedUser) {
        QUserJpaEntity user = QUserJpaEntity.userJpaEntity;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(user.status.eq(AccountStatus.fromValue(AppConstants.STATUS_ACTIVE)));
        builder.or(user.username.eq(quickSearchValueParam))
                .or(user.firstName.eq(quickSearchValueParam))
                .or(user.lastName.eq(quickSearchValueParam))
                .or(user.email.eq(quickSearchValueParam));
        return userRepository.findAll(builder, pageRequest);
    }

    private Long countUsersDb(UsersSearchRequestDTO searchObj, UserJpaEntity loggedUser) {
        Predicate predicate = getSearchPredicate(searchObj, loggedUser);
        return userRepository.count(predicate);
    }

    private List<UserDTO> findAllUsersForExportDb(UsersSearchRequestDTO searchObj, UserJpaEntity loggedUser) {
        Predicate predicate = getSearchPredicate(searchObj, loggedUser);
        List<UserJpaEntity> users = StreamSupport.stream(userRepository.findAll(predicate).spliterator(), false).toList();
        return convertToDTOList(users, true);
    }

    private Predicate getSearchPredicate(UsersSearchRequestDTO searchObj, UserJpaEntity loggedUser) {
        QUserJpaEntity user = QUserJpaEntity.userJpaEntity;
        BooleanBuilder builder = new BooleanBuilder();
        String searchMethod = searchObj.getSearchMethod();
        boolean isAdmin = UserUtil.hasRole(loggedUser, AuthorityConstants.ROLE_SYSTEM_ADMIN);
        if (!isAdmin) {
            builder.and(user.status.ne(AccountStatus.DELETED));
        }
        if (AppConstants.SEARCH_TYPE_AND.equals(searchMethod)) {
            builder = setSearchMethodAndCriteria(searchObj, builder, user);
        } else {
            builder = setSearchMethodOrCriteria(searchObj, builder, user);
        }
        return builder;
    }

    private BooleanBuilder setSearchMethodAndCriteria(UsersSearchRequestDTO searchObj,
                                                       BooleanBuilder builder, QUserJpaEntity user) {
        String email = searchObj.getEmail();
        String username = searchObj.getUsername();
        String name = searchObj.getName();
        String status = searchObj.getStatus();
        String roleName = searchObj.getRoleName();
        if (StringUtils.hasLength(email)) {
            builder.and(user.email.eq(email));
        }
        if (StringUtils.hasLength(username)) {
            builder.and(user.username.eq(username));
        }
        if (StringUtils.hasLength(name)) {
            builder.and(user.firstName.eq(name).or(user.lastName.eq(name)));
        }
        if (StringUtils.hasLength(status)) {
            builder.and(user.status.eq(AccountStatus.fromValue(status)));
        }
        if (StringUtils.hasLength(roleName)) {
            RoleJpaEntity role = roleRepository.findByName(roleName);
            if (role != null) {
                builder.and(user.roles.contains(role));
            }
        }
        return builder;
    }

    private BooleanBuilder setSearchMethodOrCriteria(UsersSearchRequestDTO searchObj,
                                                      BooleanBuilder builder, QUserJpaEntity user) {
        String email = searchObj.getEmail();
        String username = searchObj.getUsername();
        String name = searchObj.getName();
        String status = searchObj.getStatus();
        String roleName = searchObj.getRoleName();
        if (StringUtils.hasLength(email)) {
            builder.or(user.email.eq(email));
        }
        if (StringUtils.hasLength(username)) {
            builder.or(user.username.eq(username));
        }
        if (StringUtils.hasLength(name)) {
            builder.or(user.firstName.eq(name).or(user.lastName.eq(name)));
        }
        if (StringUtils.hasLength(status)) {
            builder.or(user.status.eq(AccountStatus.fromValue(status)));
        }
        if (StringUtils.hasLength(roleName)) {
            RoleJpaEntity role = roleRepository.findByName(roleName);
            if (role != null) {
                builder.or(user.roles.contains(role));
            }
        }
        return builder;
    }

    private UserDTO convertToDTO(UserJpaEntity user, Boolean withRoles) {
        UserDTO dto = new UserDTO();
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPublicId(user.getDomainId().toString());
        dto.setStatus(user.getStatus() != null ? user.getStatus().getValue() : null);
        if (withRoles) {
            dto.setRoles(user.getRoles().stream()
                    .map(r -> new RoleDTO(r.getId(), r.getName()))
                    .toList());
        }
        return dto;
    }

    private List<UserDTO> convertToDTOList(List<UserJpaEntity> userList, Boolean withRoles) {
        if (CollectionUtils.isEmpty(userList)) {
            return Collections.emptyList();
        }
        return userList.stream().map(user -> convertToDTO(user, withRoles)).toList();
    }

    private PageRequest toPageRequestFromPaging(Paging paging) {
        Sort sortBy = Sort.by(Sort.Direction.ASC, "id");
        if (paging.getSortingDirection() != null && paging.getSortingColumn() != null) {
            sortBy = Sort.by(Sort.Direction.valueOf(paging.getSortingDirection()), paging.getSortingColumn());
        }
        return PageRequest.of(paging.getPagingStart(), paging.getPagingSize(), sortBy);
    }

    // -----------------------------------------------------------------------
    // SearchService implementation
    // -----------------------------------------------------------------------

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

        Sort.Order primaryOrdering;
        if (StringUtils.hasLength(paging.getSortingDirection())
                && paging.getSortingDirection().equalsIgnoreCase("DESC")) {
            primaryOrdering = Sort.Order.desc(paging.getSortingColumn());
        } else {
            primaryOrdering = Sort.Order.asc(paging.getSortingColumn());
        }
        Sort.Order[] ordering;
        if (secondaryOrdering != null && !secondaryOrdering.isEmpty()) {
            List<Sort.Order> orderList = new ArrayList<>();
            orderList.add(primaryOrdering);
            orderList.addAll(secondaryOrdering);
            ordering = orderList.toArray(new Sort.Order[0]);
        } else {
            ordering = new Sort.Order[]{primaryOrdering};
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
        PageRequest output;

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
    public SearchResults<UserDTO> advancedSearchUsers(UsersSearchRequestDTO request, UserJpaEntity loggedUser)
            throws ResourceAccessException, AuthException {
        log.info("in SearchServiceBean ----> advancedSearchUsers ----> elasticSearchEnable: {}", elasticSearchEnable);
        String status = request.getStatus();
        String searchMethod = request.getSearchMethod();
        checkRequestParamsValidity(status, searchMethod);
        if (!elasticSearchEnable) {
            Page<UserJpaEntity> results = searchUsers(request, loggedUser);
            List<UserDTO> dtos = convertToDTOList(results.getContent(), false);
            return new SearchResults<>(Math.toIntExact(results.getTotalElements()), dtos);
        }
        SearchCriterion.FTSOperation operation = SearchCriterion.FTSOperation.AND;
        if (searchMethod.equals(AppConstants.SEARCH_TYPE_OR)) {
            operation = SearchCriterion.FTSOperation.OR;
        }
        List<SearchCriterion> criteria = setUserCriteria(request, operation);
        DocumentSearchRequest ftsRequest = getAdvancedSearchUsersRequestBuilder(request.getPaging(), status).criteria(criteria).build();
        return searchUsersOrThrow(ftsRequest);
    }

    protected DocumentSearchRequest.DocumentSearchRequestBuilder<?, ?> getFindUsersRequestBuilder(String status) {
        PagingFts.PagingFtsBuilder builder = PagingFts.builder();
        builder.page(0).limit(AppConstants.MAX_RESULTS_CSV_EXPORT);
        DocumentSearchRequest.DocumentSearchRequestBuilder<?, ?> requestBuilder = DocumentSearchRequest.builder()
                .type(DocumentSearchRequest.Type.ADVANCED).paging(builder.build());
        requestBuilder = addStatus(requestBuilder, status);
        return requestBuilder;
    }

    protected DocumentSearchRequest.DocumentSearchRequestBuilder<?, ?> addStatus(
            DocumentSearchRequest.DocumentSearchRequestBuilder<?, ?> requestBuilder, String status) {
        if (status.equals(AppConstants.STATUS_ACTIVE)) {
            requestBuilder.status(DocumentSearchRequest.Status.ACTIVE);
        } else if (status.equals(AppConstants.STATUS_ALL)) {
            requestBuilder.status(DocumentSearchRequest.Status.ALL);
        }
        return requestBuilder;
    }

    @Override
    public List<UserDTO> findUsersForExport(UsersSearchRequestDTO request, UserJpaEntity user)
            throws ResourceAccessException, AuthException {
        log.info("in SearchServiceBean ----> findUsersForExport ----> elasticSearchEnable: {}", elasticSearchEnable);
        String status = request.getStatus();
        String searchMethod = request.getSearchMethod();
        checkRequestParamsValidity(status, searchMethod);
        if (!elasticSearchEnable) {
            return findAllUsersForExportDb(request, user);
        }
        SearchCriterion.FTSOperation operation = SearchCriterion.FTSOperation.AND;
        if (searchMethod.equals(AppConstants.SEARCH_TYPE_OR)) {
            operation = SearchCriterion.FTSOperation.OR;
        }
        List<SearchCriterion> criteria = setUserCriteria(request, operation);
        DocumentSearchRequest ftsRequest = getFindUsersRequestBuilder(status).criteria(criteria).build();
        try {
            log.info("[FTS findUsersForExport]  ftsRequest: {}", ftsRequest);
            List<UserDocumentSearchResultsDTO> ftsResult = userFullTextSearchService.findUsers(ftsRequest);
            return convertFromFtsResultList(ftsResult);
        } catch (ResourceAccessException exc) {
            throw new ResourceAccessException("error.fts.connection.failure");
        }
    }

    protected List<UserDTO> convertFromFtsResultList(List<UserDocumentSearchResultsDTO> ftsResult) {
        if (CollectionUtils.isEmpty(ftsResult)) {
            return Collections.emptyList();
        }
        return ftsResult.stream()
                .map(this::convertFromFtsResult)
                .toList();
    }

    protected UserDTO convertFromFtsResult(UserDocumentSearchResultsDTO ftsResultDto) {
        UserDTO dto = new UserDTO();
        dto.setUsername(ftsResultDto.getUsername());
        dto.setFirstName(ftsResultDto.getFirstName());
        dto.setLastName(ftsResultDto.getLastName());
        dto.setEmail(ftsResultDto.getEmail());
        dto.setPublicId(ftsResultDto.getPublicId());
        dto.setStatus(ftsResultDto.getStatus());
        Set<RoleJpaEntity> roles = new HashSet<>(roleRepository.findByIds(ftsResultDto.getRoleIds()));
        if (!CollectionUtils.isEmpty(roles)) {
            dto.setRoles(roles.stream().map(r -> new RoleDTO(r.getId(), r.getName())).toList());
        }
        return dto;
    }

    @Override
    public Long countUsers(UsersSearchRequestDTO request, UserJpaEntity loggedUser) throws ResourceAccessException, AuthException {
        log.info("in SearchServiceBean ----> countItems ----> elasticSearchEnable: {}", elasticSearchEnable);
        String status = request.getStatus();
        String searchMethod = request.getSearchMethod();
        checkRequestParamsValidity(status, searchMethod);
        if (!elasticSearchEnable) {
            return countUsersDb(request, loggedUser);
        }
        DocumentSearchRequest.DocumentSearchRequestBuilder<?, ?> requestBuilder = getFindUsersRequestBuilder(status);
        SearchCriterion.FTSOperation operation = SearchCriterion.FTSOperation.AND;
        if (searchMethod.equals(AppConstants.SEARCH_TYPE_OR)) {
            operation = SearchCriterion.FTSOperation.OR;
        }
        List<SearchCriterion> criteria = setUserCriteria(request, operation);
        DocumentSearchRequest ftsRequest = requestBuilder.criteria(criteria).build();
        try {
            log.info(" [FTS COUNT ITEMS]  ftsRequest: {}", ftsRequest);
            return userFullTextSearchService.countUsers(ftsRequest);
        } catch (ResourceAccessException exc) {
            throw new ResourceAccessException("error.fts.connection.failure");
        }
    }

    @Override
    public SearchResults<UserDTO> quickSearchUsers(String quickSearchValueParam, UserJpaEntity loggedUser,
                                                   Integer page, Integer size, String sortField, String sortOrder)
            throws ResourceAccessException {
        log.info("in SearchServiceBean ----> quickSearchUsers ----> elasticSearchEnable: {}", elasticSearchEnable);
        if (!elasticSearchEnable) {
            PageRequest pageRequest = toPageRequest(page, size, sortField, sortOrder);
            Page<UserJpaEntity> results = quickSearchUsersDb(quickSearchValueParam, pageRequest, loggedUser);
            List<UserDTO> dtos = convertToDTOList(results.getContent(), false);
            return new SearchResults<>(Math.toIntExact(results.getTotalElements()), dtos);
        }
        DocumentSearchRequest ftsRequest = getQuickSearchUsersRequestBuilder(quickSearchValueParam, page, size, sortField, sortOrder)
                .status(DocumentSearchRequest.Status.ACTIVE)
                .build();
        return searchUsersOrThrow(ftsRequest);
    }

    protected DocumentSearchRequest.DocumentSearchRequestBuilder<?, ?> getAdvancedSearchUsersRequestBuilder(Paging paging, String status) {
        PagingFts pagingFts = addPaging(paging);
        DocumentSearchRequest.DocumentSearchRequestBuilder<?, ?> requestBuilder = DocumentSearchRequest.builder()
                .type(DocumentSearchRequest.Type.ADVANCED).paging(pagingFts);
        requestBuilder = addStatus(requestBuilder, status);
        return requestBuilder;
    }

    protected DocumentSearchRequest.DocumentSearchRequestBuilder<?, ?> getQuickSearchUsersRequestBuilder(
            String quickSearchValueParam, Integer page, Integer size, String sortField, String sortOrder) {
        List<SearchCriterion> criteria = new ArrayList<>();
        PagingFts paging = addPaging(page, size, sortField, sortOrder);
        SearchCriterion.FTSOperation operation = SearchCriterion.FTSOperation.OR;
        addTextCriterion(operation, criteria, FtsUtil.ES_USER_FIELD_USERNAME, quickSearchValueParam, SearchCriterion.SearchType.WILDCARD);
        addTextCriterion(operation, criteria, FtsUtil.ES_USER_FIELD_FIRST_NAME + FtsUtil.ES_FIELD_TYPE_NGRAM, quickSearchValueParam, SearchCriterion.SearchType.MATCH);
        addTextCriterion(operation, criteria, FtsUtil.ES_USER_FIELD_LAST_NAME + FtsUtil.ES_FIELD_TYPE_NGRAM, quickSearchValueParam, SearchCriterion.SearchType.MATCH);
        addTextCriterion(operation, criteria, FtsUtil.ES_USER_FIELD_EMAIL + FtsUtil.ES_FIELD_TYPE_NGRAM, quickSearchValueParam, SearchCriterion.SearchType.MATCH);
        return DocumentSearchRequest.builder().type(DocumentSearchRequest.Type.QUICK).paging(paging).criteria(criteria).status(DocumentSearchRequest.Status.ALL);
    }

    protected SearchResults<UserDTO> searchUsersOrThrow(DocumentSearchRequest ftsRequest)
            throws ResourceAccessException {
        try {
            log.info(" [FTS SEARCH]  ftsRequest: {}", ftsRequest);
            UserSearchResponseDTO ftsResponse = userFullTextSearchService.searchUsers(ftsRequest);
            int count = ftsResponse.getTotalElements().intValue();
            log.info(" [FTS SEARCH]  results count: {}", count);
            return new SearchResults<>(count, convertFromFtsResultList(ftsResponse.getContent()));
        } catch (ResourceAccessException exc) {
            throw new ResourceAccessException("error.fts.connection.failure");
        }
    }

    protected List<SearchCriterion> setUserCriteria(UsersSearchRequestDTO request, SearchCriterion.FTSOperation operation) {
        List<SearchCriterion> criteria = new ArrayList<>();
        String username = request.getUsername();
        String name = request.getName();
        String email = request.getEmail();
        String roleName = request.getRoleName();
        addTextCriterion(operation, criteria, FtsUtil.ES_USER_FIELD_USERNAME, username, SearchCriterion.SearchType.WILDCARD);
        addTextCriterion(operation, criteria, FtsUtil.ES_USER_FIELD_ROLE_NAMES, roleName);
        addTextCriterion(SearchCriterion.FTSOperation.OR, criteria, FtsUtil.ES_USER_FIELD_FIRST_NAME + FtsUtil.ES_FIELD_TYPE_NGRAM, name, SearchCriterion.SearchType.MATCH);
        addTextCriterion(SearchCriterion.FTSOperation.OR, criteria, FtsUtil.ES_USER_FIELD_LAST_NAME + FtsUtil.ES_FIELD_TYPE_NGRAM, name, SearchCriterion.SearchType.MATCH);
        addTextCriterion(operation, criteria, FtsUtil.ES_USER_FIELD_EMAIL + FtsUtil.ES_FIELD_TYPE_NGRAM, email, SearchCriterion.SearchType.MATCH);
        return criteria;
    }

    protected void logSearchCriterion(String field, Object value) {
        log.info(" [USER SEARCH] - {}: {}", field, value);
    }

    protected void addTextCriterion(SearchCriterion.FTSOperation operation, List<SearchCriterion> criteria, String field, String value) {
        if (StringUtils.hasLength(value)) {
            logSearchCriterion(field, value);
            SearchCriterion criterion = SearchCriterion.builder().type(SearchCriterion.Type.TEXT)
                    .operation(operation).searchType(SearchCriterion.SearchType.MATCH).field(field)
                    .values(List.of(value)).build();
            criteria.add(criterion);
        }
    }

    protected void addTextCriterion(SearchCriterion.FTSOperation operation, List<SearchCriterion> criteria, String field, String value,
                                    SearchCriterion.SearchType searchType) {
        if (StringUtils.hasLength(value)) {
            logSearchCriterion(field, value);
            SearchCriterion criterion = SearchCriterion.builder().type(SearchCriterion.Type.TEXT).operation(operation).searchType(searchType)
                    .field(field).values(List.of(value)).build();
            criteria.add(criterion);
        }
    }

    protected void addIntegerCriterion(SearchCriterion.FTSOperation operation, List<SearchCriterion> criteria, String field, Integer value) {
        if (Objects.nonNull(value)) {
            logSearchCriterion(field, value);
            SearchCriterion criterion = SearchCriterion.builder().type(SearchCriterion.Type.INTEGER).operation(operation)
                    .searchType(SearchCriterion.SearchType.MATCH).field(field).values(List.of(value)).build();
            criteria.add(criterion);
        }
    }

    protected void addIntegerCriterion(SearchCriterion.FTSOperation operation, List<SearchCriterion> criteria, String field, Integer value,
                                       SearchCriterion.SearchType searchType) {
        if (Objects.nonNull(value)) {
            logSearchCriterion(field, value);
            SearchCriterion criterion = SearchCriterion.builder().type(SearchCriterion.Type.INTEGER).operation(operation).searchType(searchType)
                    .field(field).values(List.of(value)).build();
            criteria.add(criterion);
        }
    }

    protected void addBooleanCriterion(SearchCriterion.FTSOperation operation, List<SearchCriterion> criteria, String field, Boolean value) {
        if (Objects.nonNull(value)) {
            logSearchCriterion(field, value);
            SearchCriterion criterion = SearchCriterion.builder().type(SearchCriterion.Type.BOOLEAN).operation(operation)
                    .searchType(SearchCriterion.SearchType.MATCH).field(field).values(List.of(value)).build();
            criteria.add(criterion);
        }
    }

    protected PagingFts addPaging(Paging paging) {
        PagingFts.PagingFtsBuilder pagingBuilder = PagingFts.builder();
        if (Objects.isNull(paging)) {
            Integer defaultPage = Paging.DEFAULT_PAGE_START;
            Integer defaultSize = Paging.DEFAULT_PAGE_SIZE;
            pagingBuilder.page(defaultPage).limit(defaultSize);
            return pagingBuilder.build();
        }
        Integer calculatedPage = calculatePage(paging.getPagingStart(), paging.getPagingSize());
        pagingBuilder.page(calculatedPage).limit(paging.getPagingSize());
        String sortField = paging.getSortingColumn();
        String sortOrder = paging.getSortingDirection();
        Map<String, String> sortingColsMap = getUserColumnsForFTSSortOrGroupOps();

        log.info(" [addPaging] - sortField: {}", sortField);
        log.info(" [addPaging] - sortOrder: {}", sortOrder);
        if (StringUtils.hasLength(sortField)) {
            Optional.ofNullable(sortingColsMap.get(sortField)).ifPresentOrElse(sortFieldMapped -> {
                log.info(" [addPaging] - sortFieldMapped: {}", sortFieldMapped);
                pagingBuilder.sortFields(List.of(sortFieldMapped));
            }, () -> {
                log.error(" ERROR-addPaging: Could not find mapping for SortBy={} (Setting default) ", sortField);
                pagingBuilder.sortFields(List.of(FtsUtil.ES_USER_FIELD_ID));
            });
        }
        if (StringUtils.hasLength(sortOrder)) {
            pagingBuilder.sortDirection(sortOrder);
        }
        return pagingBuilder.build();
    }

    protected PagingFts addPaging(Integer page, Integer size, String sortField, String sortOrder) {
        Integer calculatedPage = calculatePage(page, size);
        PagingFts.PagingFtsBuilder pagingBuilder = PagingFts.builder().page(calculatedPage).limit(size);
        if (StringUtils.hasLength(sortField)) {
            pagingBuilder.sortFields(List.of(getUserColumnsForFTSSortOrGroupOps().get(sortField)));
        }
        if (StringUtils.hasLength(sortOrder)) {
            pagingBuilder.sortDirection(sortOrder);
        }
        return pagingBuilder.build();
    }

    protected Map<String, String> getUserColumnsForFTSSortOrGroupOps() {
        return FtsUtil.getUserColumnsForFTSSortOrGroupOps();
    }

    protected Integer calculatePage(Integer page, Integer size) {
        return page / size;
    }
}
