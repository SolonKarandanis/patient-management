package com.pm.authservice.service;

import com.pm.authservice.dto.*;
import com.pm.authservice.event.UserRegistrationCompleteEvent;
import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.exception.NotFoundException;
import com.pm.authservice.model.*;
import com.pm.authservice.repository.UserRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.StreamSupport;


@Service
@Transactional(readOnly = true)
public class UserServiceBean extends GenericServiceBean implements UserService{
    protected static final String USER_NOT_FOUND="error.user.not.found";
    private static final Logger log = LoggerFactory.getLogger(UserServiceBean.class);

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final VerificationTokenService verificationTokenService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceBean(
            UserRepository userRepository,
            RoleService roleService,
            VerificationTokenService verificationTokenService,
            PasswordEncoder passwordEncoder
    ){
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.verificationTokenService = verificationTokenService;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDTO convertToDTO(UserEntity user, Boolean withRoles) {
        UserDTO dto = new UserDTO();
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPublicId(user.getPublicId().toString());
        dto.setStatus(user.getStatus().toString());
        if(withRoles){
            dto.setRoles(roleService.convertToDtoList(user.getRoles()));
        }
        return dto;
    }

    @Transactional
    @Override
    public UserEntity convertToEntity(UserDTO dto) {
        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        String publicId=dto.getPublicId();
        user.setPublicId(UUID.fromString(publicId));
        if(StringUtils.hasLength(publicId)){
            Integer userId=userRepository.findIdByPublicId(UUID.fromString(publicId)).orElse(null);
            user.setId(userId);
        }
        user.setStatus(AccountStatus.valueOf(dto.getStatus()));
        if(!CollectionUtils.isEmpty(dto.getRoles())){
            List<Integer> roleIds= dto.getRoles().stream().map(RoleDTO::getId).toList();
            Set<RoleEntity> roles = new HashSet<>(roleService.findByIds(roleIds));
            user.setRoles(roles);
        }
        return user;
    }

    @Override
    public List<UserDTO> convertToDTOList(List<UserEntity> userList, Boolean withRoles) {
        if(CollectionUtils.isEmpty(userList)){
            return Collections.emptyList();
        }
        return userList.stream()
                .map(user->convertToDTO(user,withRoles))
                .toList();
    }

    @Override
    public UserEntity findById(Integer id) throws NotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    @Override
    public UserEntity findByPublicId(String publicId) throws NotFoundException {
        return userRepository.findByPublicId(UUID.fromString(publicId))
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    @Override
    public UserEntity findByEmail(String email) throws NotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    @Transactional
    @Override
    public void deleteUser(String publicId) throws NotFoundException {
        Optional<UserEntity> usrOpt  =userRepository.findByPublicId(UUID.fromString(publicId));
        usrOpt.ifPresentOrElse(
                userRepository::delete,
                ()-> new NotFoundException(USER_NOT_FOUND)
        );
    }

    @Override
    public Page<UserEntity> searchUsers(UsersSearchRequestDTO searchObj) {
        PageRequest pageRequest = toPageRequest(searchObj.getPaging());
        Predicate predicate = getSearchPredicate(searchObj);
        return userRepository.findAll(predicate,pageRequest);
    }

    @Override
    public Long countUsers(UsersSearchRequestDTO searchObj) {
        Predicate predicate = getSearchPredicate(searchObj);
        return userRepository.count(predicate);
    }

    @Override
    public List<UserDTO> findAllUsersForExport(UsersSearchRequestDTO searchObj) {
        Predicate predicate = getSearchPredicate(searchObj);
        List<UserEntity> users=StreamSupport.stream(userRepository.findAll(predicate).spliterator(), false).toList();
        List<UserDTO> results=new ArrayList<>();
        for(UserEntity user: users){
            UserDTO dto = convertToDTO(user,true);
            results.add(dto);
        }
        return results;
    }

    @Transactional
    @Override
    public UserEntity registerUser(CreateUserDTO dto, String applicationUrl) throws BusinessException {
        Optional<UserEntity> userNameMaybe  = userRepository.findByUsername(dto.getUsername());

        if(userNameMaybe.isPresent()){
            throw new BusinessException("error.username.exists");
        }

        Optional<UserEntity> emailMaybe  = userRepository.findByEmail(dto.getEmail());
        if(emailMaybe.isPresent()){
            throw new BusinessException("error.email.exists");
        }

        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setStatus(AccountStatus.INACTIVE);
        user.setIsVerified(Boolean.FALSE);
        UUID uuid = UUID.randomUUID();
        user.setPublicId(uuid);
        RoleEntity role = roleService.findByName(dto.getRole());
        user.setRoles(Set.of(role));
        user = userRepository.save(user);
        getPublisher().publishEvent(new UserRegistrationCompleteEvent(user, applicationUrl));
        return user;
    }

    @Transactional
    @Override
    public UserEntity updateUser(String publicId, UpdateUserDTO dto) throws NotFoundException {
        UserEntity user = findByPublicId(publicId);
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        RoleEntity role = roleService.findByName(dto.getRole());
        user.setRoles(Set.of(role));
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public UserEntity activateUser(UserEntity user) throws BusinessException {
        user.activate();
        userRepository.save(user);
        return user;
    }

    @Transactional
    @Override
    public UserEntity deactivateUser(UserEntity user) throws BusinessException {
        user.deactivate();
        userRepository.save(user);
        return user;
    }

    @Override
    public void verifyEmail(String token) throws BusinessException {
        VerificationTokenEntity verificationToken = verificationTokenService.findByToken(token);
        Boolean verificationResult = verificationTokenService.validateToken(verificationToken);
        if(verificationResult){
            UserEntity user = verificationToken.getUser();
            user.setIsVerified(Boolean.TRUE);
            userRepository.save(user);
        }
    }

    protected Predicate getSearchPredicate(UsersSearchRequestDTO searchObj){
        QUserEntity user = QUserEntity.userEntity;
        BooleanBuilder builder = new BooleanBuilder();
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
            RoleEntity role = roleService.findByName(roleName);
            if(role != null){
                builder.and(user.roles.contains(role));
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
}
