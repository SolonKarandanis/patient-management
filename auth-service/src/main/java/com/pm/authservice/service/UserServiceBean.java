package com.pm.authservice.service;

import com.pm.authservice.dto.*;
import com.pm.authservice.event.UserRegistrationCompleteEvent;
import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.exception.NotFoundException;
import com.pm.authservice.model.*;
import com.pm.authservice.repository.RoleRepository;
import com.pm.authservice.repository.UserRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
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
public class UserServiceBean implements UserService{
    protected static final String USER_NOT_FOUND="error.user.not.found";
    private static final Logger log = LoggerFactory.getLogger(UserServiceBean.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenService verificationTokenService;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;
    private final ApplicationEventPublisher publisher;

    public UserServiceBean(
            UserRepository userRepository,
            RoleRepository roleRepository,
            VerificationTokenService verificationTokenService,
            PasswordEncoder passwordEncoder,
            MessageSource messageSource,
            ApplicationEventPublisher publisher
    ){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.verificationTokenService = verificationTokenService;
        this.passwordEncoder = passwordEncoder;
        this.messageSource = messageSource;
        this.publisher = publisher;
    }


    @Override
    public UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPublicId(user.getPublicId().toString());
        dto.setStatus(user.getStatus().toString());
        if(!CollectionUtils.isEmpty(user.getRoles())) {
            List<RoleDTO> roleDTOS = new ArrayList<>();
            for(Role role: user.getRoles()){
                RoleDTO roleDTO = new RoleDTO(role.getId(),role.getName());
                roleDTOS.add(roleDTO);
            }
            dto.setRoles(roleDTOS);
        }
        return dto;
    }

    @Transactional
    @Override
    public User convertToEntity(UserDTO dto) {
        User user = new User();
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
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
            user.setRoles(roles);
        }
        return user;
    }

    @Override
    public List<UserDTO> convertToDTOList(List<User> userList) {
        if(CollectionUtils.isEmpty(userList)){
            return Collections.emptyList();
        }
        return userList.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public User findById(Integer id) throws NotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    @Override
    public User findByPublicId(String publicId) throws NotFoundException {
        return userRepository.findByPublicId(UUID.fromString(publicId))
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    @Override
    public User findByEmail(String email) throws NotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    @Transactional
    @Override
    public void deleteUser(String publicId) throws NotFoundException {
        Optional<User> usrOpt  =userRepository.findByPublicId(UUID.fromString(publicId));
        usrOpt.ifPresentOrElse(
                userRepository::delete,
                ()-> new NotFoundException(USER_NOT_FOUND)
        );
    }

    @Override
    public Page<User> searchUsers(UsersSearchRequestDTO searchObj) {
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
        List<User> users=StreamSupport.stream(userRepository.findAll(predicate).spliterator(), false).toList();
        List<UserDTO> results=new ArrayList<>();
        for(User user: users){
            UserDTO dto = convertToDTO(user);
            results.add(dto);
        }
        return results;
    }

    @Transactional
    @Override
    public User registerUser(CreateUserDTO dto,String applicationUrl) throws BusinessException {
        Optional<User> userNameMaybe  = userRepository.findByUsername(dto.getUsername());

        if(userNameMaybe.isPresent()){
            throw new BusinessException("error.username.exists");
        }

        Optional<User> emailMaybe  = userRepository.findByEmail(dto.getEmail());
        if(emailMaybe.isPresent()){
            throw new BusinessException("error.email.exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setStatus(AccountStatus.INACTIVE);
        user.setIsVerified(Boolean.FALSE);
        UUID uuid = UUID.randomUUID();
        user.setPublicId(uuid);
        Role role = roleRepository.findByName(dto.getRole());
        user.setRoles(Set.of(role));
        user = userRepository.save(user);
        publisher.publishEvent(new UserRegistrationCompleteEvent(user, applicationUrl));
        return user;
    }

    @Transactional
    @Override
    public User updateUser(String publicId, UpdateUserDTO dto) throws NotFoundException {
        User user = findByPublicId(publicId);
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public User activateUser(User user) throws BusinessException {
        if(AccountStatus.ACTIVE.equals(user.getStatus())){
            throw new BusinessException("error.user.already.active");
        }
        user.setStatus(AccountStatus.ACTIVE);
        user.setIsEnabled(Boolean.TRUE);
        user.setIsVerified(Boolean.TRUE);
        userRepository.save(user);
        return user;
    }

    @Transactional
    @Override
    public User deactivateUser(User user) throws BusinessException {
        if(AccountStatus.INACTIVE.equals(user.getStatus())){
            throw new BusinessException("error.user.already.inactive");
        }
        user.setStatus(AccountStatus.INACTIVE);
        user.setIsEnabled(Boolean.FALSE);
        userRepository.save(user);
        return user;
    }

    @Override
    public void verifyEmail(String token) throws BusinessException {
        VerificationToken verificationToken = verificationTokenService.findByToken(token);
        Boolean verificationResult = verificationTokenService.validateToken(verificationToken);
        if(verificationResult){
            User user = verificationToken.getUser();
            user.setIsVerified(Boolean.TRUE);
            userRepository.save(user);
        }
    }

    protected Predicate getSearchPredicate(UsersSearchRequestDTO searchObj){
        QUser user = QUser.user;
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
            builder.and(user.status.eq(AccountStatus.valueOf(status)));
        }
        if(StringUtils.hasLength(roleName)){
            Role role = roleRepository.findByName(roleName);
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


    protected String translate(String key) {
        return Optional.ofNullable(messageSource.getMessage(key, null, getDefaultLocale())).orElse(key);
    }


    protected String translate(String key, Locale locale) {
        return Optional.ofNullable(messageSource.getMessage(key, null, locale)).orElse(key);
    }

    protected Locale getDefaultLocale() {
        return Locale.ENGLISH;
    }
}
