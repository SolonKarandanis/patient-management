package com.pm.authservice.user.service;

import com.pm.authservice.service.GenericService;
import com.pm.authservice.user.dto.ChangePasswordDTO;
import com.pm.authservice.user.dto.RoleDTO;
import com.pm.authservice.dto.Paging;
import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.exception.NotFoundException;
import com.pm.authservice.domain.model.AccountStatus;
import com.pm.authservice.infrastructure.persistence.entity.RoleJpaEntity;
import com.pm.authservice.model.VerificationTokenEntity;
import com.pm.authservice.service.VerificationTokenService;
import com.pm.authservice.user.dto.*;
import com.pm.authservice.user.event.*;
import com.pm.authservice.outbox.service.OutboxService;
import com.pm.authservice.infrastructure.persistence.entity.QUserJpaEntity;
import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import com.pm.authservice.user.repository.RoleRepository;
import com.pm.authservice.user.repository.UserRepository;
import com.pm.authservice.user.repository.projections.MinMaxUserId;
import com.pm.authservice.util.AppConstants;
import com.pm.authservice.util.AuthorityConstants;
import com.pm.authservice.util.UserUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.StreamSupport;


@Service
@Transactional(readOnly = true)
public class UserServiceBean implements UserService{
    protected static final String USER_NOT_FOUND="error.user.not.found";
    private static final Logger log = LoggerFactory.getLogger(UserServiceBean.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenService verificationTokenService;
    private final PasswordEncoder passwordEncoder;
    private final GenericService genericService;
    private final OutboxService outboxService;

    public UserServiceBean(
            UserRepository userRepository,
            RoleRepository roleRepository,
            VerificationTokenService verificationTokenService,
            PasswordEncoder passwordEncoder, GenericService genericService,
            OutboxService outboxService
    ){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.verificationTokenService = verificationTokenService;
        this.passwordEncoder = passwordEncoder;
        this.genericService = genericService;
        this.outboxService = outboxService;
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public UserDTO convertToDTO(UserJpaEntity user, Boolean withRoles) {
        UserDTO dto = new UserDTO();
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPublicId(user.getDomainId().toString());
        dto.setStatus(user.getStatus() != null ? user.getStatus().getValue() : null);
        if(withRoles){
            dto.setRoles(user.getRoles().stream()
                    .map(r -> new RoleDTO(r.getId(), r.getName()))
                    .toList());
        }
        return dto;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public UserJpaEntity convertToEntity(UserDTO dto) {
        UserJpaEntity user = new UserJpaEntity();
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        String publicId=dto.getPublicId();
        if(StringUtils.hasLength(publicId)){
            user.setDomainId(UUID.fromString(publicId));
            Integer userId=userRepository.findIdByDomainId(UUID.fromString(publicId)).orElse(null);
            user.setId(userId);
        }
        user.setStatus(AccountStatus.fromValue(dto.getStatus()));
        if(!CollectionUtils.isEmpty(dto.getRoles())){
            List<Integer> roleIds= dto.getRoles().stream().map(RoleDTO::getId).toList();
            Set<RoleJpaEntity> roles = new HashSet<>(roleRepository.findByIds(roleIds));
            user.setRoles(roles);
        }
        return user;
    }

    @Override
    public List<UserDTO> convertToDTOList(List<UserJpaEntity> userList, Boolean withRoles) {
        if(CollectionUtils.isEmpty(userList)){
            return Collections.emptyList();
        }
        return userList.stream()
                .map(user->convertToDTO(user,withRoles))
                .toList();
    }

    @Override
    public UserJpaEntity findById(Integer id) throws NotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    @Override
    public UserJpaEntity findByPublicId(String publicId) throws NotFoundException {
        return userRepository.findByDomainId(UUID.fromString(publicId))
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    @Override
    public List<String> getUserPermissions(String publicId) throws NotFoundException {
        UserJpaEntity user= userRepository.findByDomainId(UUID.fromString(publicId))
                            .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        return userRepository.findUserPermissions(user.getId());
    }

    @Override
    public UserJpaEntity findByEmail(String email) throws NotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    @Override
    public List<UserJpaEntity> findUsersToBeIndexedByIdRange(Integer minId, Integer maxId) {
        return userRepository.findUsersByIdRange(minId,maxId);
    }

    @Override
    public MinMaxUserIdDTO getMinAndMaxUserId() {
        MinMaxUserId result =userRepository.findMinAndMaxUserId();
        return new MinMaxUserIdDTO(result.getMinId(),result.getMaxId());
    }

    @Override
    public Page<UserJpaEntity> searchUsers(UsersSearchRequestDTO searchObj,UserJpaEntity loggedUser) {
        PageRequest pageRequest = toPageRequest(searchObj.getPaging());
        Predicate predicate = getSearchPredicate(searchObj,loggedUser);
        return userRepository.findAll(predicate,pageRequest);
    }

    @Override
    public Page<UserJpaEntity> quickSearchUsers(String quickSearchValueParam, PageRequest pageRequest, UserJpaEntity loggedUser) {
        PageRequest transformedPageRequest = transformPageSorting(pageRequest);
        Predicate searchPredicate = getUserQuickSearchPredicate(quickSearchValueParam, loggedUser);
        return userRepository.findAll(searchPredicate,transformedPageRequest);
    }


    protected PageRequest transformPageSorting(PageRequest pageRequest) {
        return genericService.transformPageSorting(pageRequest, usersSortingFieldsMap(), getUsersSortingFields());
    }

    protected Map<String, String> usersSortingFieldsMap() {
        HashMap<String, String> result = new HashMap<>();
        result.put("id", "id");
        result.put("publicId", "domainId");
        result.put("username", "username");
        result.put("firstName", "firstName");
        result.put("lastName", "lastName");
        result.put("status", "status");
        result.put("email", "email");
        result.put("isEnabled", "isEnabled");
        result.put("isVerified", "isVerified");
        result.put("createdDate", "createdDate");
        return result;
    }

    protected Set<String> getUsersSortingFields() {
        return Set.of("id", "publicId", "username", "firstName", "lastName", "status", "email", "isEnabled", "isVerified", "createdDate");
    }

    protected Predicate getUserQuickSearchPredicate(String quickSearchValueParam, UserJpaEntity loggedUser) {
        QUserJpaEntity user = QUserJpaEntity.userJpaEntity;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(user.status.eq(AccountStatus.valueOf(AppConstants.STATUS_ACTIVE)));
        builder.or(user.username.eq(quickSearchValueParam))
                .or(user.firstName.eq(quickSearchValueParam))
                .or(user.lastName.eq(quickSearchValueParam))
                .or(user.email.eq(quickSearchValueParam));
        return builder;
    }

    @Override
    public Long countUsers(UsersSearchRequestDTO searchObj, UserJpaEntity loggedUser) {
        Predicate predicate = getSearchPredicate(searchObj,loggedUser);
        return userRepository.count(predicate);
    }

    @Override
    public List<UserDTO> findAllUsersForExport(UsersSearchRequestDTO searchObj, UserJpaEntity loggedUser) {
        Predicate predicate = getSearchPredicate(searchObj,loggedUser);
        List<UserJpaEntity> users=StreamSupport.stream(userRepository.findAll(predicate).spliterator(), false).toList();
        List<UserDTO> results=new ArrayList<>();
        for(UserJpaEntity user: users){
            UserDTO dto = convertToDTO(user,true);
            results.add(dto);
        }
        return results;
    }

    protected Predicate getSearchPredicate(UsersSearchRequestDTO searchObj,UserJpaEntity loggedUser){
        QUserJpaEntity user = QUserJpaEntity.userJpaEntity;
        BooleanBuilder builder = new BooleanBuilder();
        String searchMethod = searchObj.getSearchMethod();
        boolean isAdmin = UserUtil.hasRole(loggedUser, AuthorityConstants.ROLE_SYSTEM_ADMIN);
        if(!isAdmin){
            builder.and(user.status.ne(AccountStatus.DELETED));
        }
        if(AppConstants.SEARCH_TYPE_AND.equals(searchMethod)){
            builder= setSearchMethodAndCriteria(searchObj,builder,user);
        }
        else{
            builder= setSearchMethodOrCriteria(searchObj,builder,user);
        }
        return builder;
    }

    protected BooleanBuilder setSearchMethodAndCriteria(
            UsersSearchRequestDTO searchObj,
            BooleanBuilder builder,
            QUserJpaEntity user){
        String email =searchObj.getEmail();
        String username = searchObj.getUsername();
        String name= searchObj.getName();
        String status = searchObj.getStatus();
        String roleName = searchObj.getRoleName();

        if(StringUtils.hasLength(email)){
            builder.and(user.email.eq(email));
        }
        if(StringUtils.hasLength(username)){
            builder.and(user.username.eq(username));
        }
        if(StringUtils.hasLength(name)){
            builder.and(user.firstName.eq(name).or(user.lastName.eq(name)));
        }
        if(StringUtils.hasLength(status)){
            builder.and(user.status.eq(AccountStatus.fromValue(status)));
        }
        if(StringUtils.hasLength(roleName)){
            RoleJpaEntity role = roleRepository.findByName(roleName);
            if(role != null){
                builder.and(user.roles.contains(role));
            }
        }
        return builder;
    }

    protected BooleanBuilder setSearchMethodOrCriteria(
            UsersSearchRequestDTO searchObj,
            BooleanBuilder builder,
            QUserJpaEntity user){
        String email =searchObj.getEmail();
        String username = searchObj.getUsername();
        String name= searchObj.getName();
        String status = searchObj.getStatus();
        String roleName = searchObj.getRoleName();

        if(StringUtils.hasLength(email)){
            builder.or(user.email.eq(email));
        }
        if(StringUtils.hasLength(username)){
            builder.or(user.username.eq(username));
        }
        if(StringUtils.hasLength(name)){
            builder.or(user.firstName.eq(name).or(user.lastName.eq(name)));
        }
        if(StringUtils.hasLength(status)){
            builder.or(user.status.eq(AccountStatus.fromValue(status)));
        }
        if(StringUtils.hasLength(roleName)){
            RoleJpaEntity role = roleRepository.findByName(roleName);
            if(role != null){
                builder.or(user.roles.contains(role));
            }
        }
        return builder;
    }

    protected PageRequest toPageRequest(Paging paging) {
        Sort sortBy = Sort.by(Sort.Direction.ASC,"id");
        if(Objects.nonNull(paging.getSortingDirection()) && Objects.nonNull(paging.getSortingColumn())){
            sortBy = Sort.by(Sort.Direction.valueOf(paging.getSortingDirection()), paging.getSortingColumn());
        }
        return PageRequest.of(paging.getPagingStart(), paging.getPagingSize(), sortBy);
    }

    protected void validateUsernameExistence(String username)throws BusinessException{
        Optional<UserJpaEntity> userNameMaybe  = userRepository.findByUsername(username);
        if(userNameMaybe.isPresent()){
            throw new BusinessException("error.username.exists");
        }
    }

    protected void validateEmailExistence(String email)throws BusinessException{
        Optional<UserJpaEntity> emailMaybe  = userRepository.findByEmail(email);
        if(emailMaybe.isPresent()){
            throw new BusinessException("error.email.exists");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public UserJpaEntity registerUser(CreateUserDTO dto, String applicationUrl) throws BusinessException {
        validateUsernameExistence(dto.getUsername());
        validateEmailExistence(dto.getEmail());
        validatePasswordChange(dto.getPassword(), dto.getConfirmPassword(), true);
        UserJpaEntity user = new UserJpaEntity();
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setStatus(AccountStatus.INACTIVE);
        user.setIsVerified(Boolean.FALSE);
        LocalDate today = LocalDate.now();
        user.setLastModifiedDate(today);
        user.setCreatedDate(today);
        UUID uuid = UUID.randomUUID();
        user.setDomainId(uuid);
        RoleJpaEntity role = roleRepository.findByName(dto.getRole());
        user.setRoles(Set.of(role));
        user = userRepository.save(user);
        outboxService.createUserEvent(user, AppConstants.OUTBOX_USER_CREATED);
        genericService.getPublisher().publishEvent(new UserRegistrationEvent(user, applicationUrl));
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public UserJpaEntity updateUser(String publicId, UpdateUserDTO dto) throws NotFoundException {
        UserJpaEntity user = findByPublicId(publicId);
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        LocalDate today = LocalDate.now();
        user.setLastModifiedDate(today);
        RoleJpaEntity role = roleRepository.findByName(dto.getRole());
        user.removeRoles();
        user.addRole(role);
        genericService.getPublisher().publishEvent(new UserUpdateEvent(user));
        user = userRepository.save(user);
        outboxService.createUserEvent(user, AppConstants.OUTBOX_USER_UPDATED);
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public UserJpaEntity activateUser(UserJpaEntity user) throws BusinessException {
        user.activate();
        user = userRepository.save(user);
        outboxService.createUserEvent(user, AppConstants.OUTBOX_USER_ACTIVATED);
        genericService.getPublisher().publishEvent(new UserActivationEvent(user));
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public UserJpaEntity deactivateUser(UserJpaEntity user) throws BusinessException {
        user.deactivate();
        user = userRepository.save(user);
        outboxService.createUserEvent(user, AppConstants.OUTBOX_USER_DEACTIVATED);
        genericService.getPublisher().publishEvent(new UserDeactivationEvent(user));
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteUser(String publicId) throws NotFoundException {
        Optional<UserJpaEntity> usrOpt  =userRepository.findByDomainId(UUID.fromString(publicId));
        usrOpt.ifPresent(usr->{
            userRepository.delete(usr);
            outboxService.createUserEvent(usr, AppConstants.OUTBOX_USER_DELETED);
            genericService.getPublisher().publishEvent(new UserDeletionEvent(usr));
        });
    }

    @Override
    public void verifyEmail(String token) throws BusinessException {
        VerificationTokenEntity verificationToken = verificationTokenService.findByToken(token);
        Boolean verificationResult = verificationTokenService.validateToken(verificationToken);
        if(verificationResult){
            UserJpaEntity user = verificationToken.getUser();
            user.setIsVerified(Boolean.TRUE);
            user = userRepository.save(user);
            outboxService.createUserEvent(user, AppConstants.OUTBOX_USER_VERIFIED);
        }
    }

    @Override
    public void validatePasswordChange( String newPassword, String confirmPassword, boolean isPasswordRequired) throws BusinessException {
        if(isPasswordRequired && !StringUtils.hasLength(newPassword)){
            throw new BusinessException("error.password.required");
        }
        if(isPasswordRequired && !StringUtils.hasLength(confirmPassword)){
            throw new BusinessException("error.password.confirm.required");
        }
        if (isPasswordRequired && !newPassword.equals(confirmPassword)) {
            throw new BusinessException("error.password.confirm");
        }
        if(!isPasswordRequired && StringUtils.hasLength(newPassword) && !StringUtils.hasLength(confirmPassword)
                && !newPassword.equals(confirmPassword)){
            throw new BusinessException("error.password.confirm");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public UserJpaEntity changePassword(UserJpaEntity user, ChangePasswordDTO dto)throws BusinessException {
        validatePasswordChange(dto.getPassword(), dto.getConfirmPassword(), true);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        LocalDate today = LocalDate.now();
        user.setLastModifiedDate(today);
        user = userRepository.save(user);
        outboxService.createUserEvent(user, AppConstants.OUTBOX_USER_UPDATED);
        return user;
    }
}
