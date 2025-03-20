package com.pm.authservice.service;

import com.pm.authservice.dto.*;
import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.exception.NotFoundException;
import com.pm.authservice.model.AccountStatus;
import com.pm.authservice.model.Role;
import com.pm.authservice.model.User;
import com.pm.authservice.repository.RoleRepository;
import com.pm.authservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


@Service
@Transactional(readOnly = true)
public class UserServiceBean implements UserService{
    protected static final String USER_NOT_FOUND="error.user.not.found";
    private static final Logger log = LoggerFactory.getLogger(UserServiceBean.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;

    public UserServiceBean(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            MessageSource messageSource
    ){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.messageSource = messageSource;
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
            Integer userId=null;
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
        return List.of();
    }

    @Override
    public User findById(Integer id) throws NotFoundException {
        return null;
    }

    @Override
    public User findByPublicId(String publicId) throws NotFoundException {
        return null;
    }

    @Override
    public User findByEmail(String email) throws NotFoundException {
        return null;
    }

    @Transactional
    @Override
    public void deleteUser(String publicId) throws NotFoundException {

    }

    @Override
    public Page<User> searchUsers(UsersSearchRequestDTO searchObj) {
        return null;
    }

    @Override
    public Long countUsers(UsersSearchRequestDTO searchObj) {
        return 0L;
    }

    @Override
    public List<User> findAllUsersForExport(UsersSearchRequestDTO searchObj) {
        return List.of();
    }

    @Transactional
    @Override
    public User registerUser(CreateUserDTO dto) throws BusinessException {
        return null;
    }

    @Transactional
    @Override
    public User updateUser(String publicId, UpdateUserDTO dto) throws NotFoundException {
        return null;
    }

    @Transactional
    @Override
    public User activateUser(User user) throws BusinessException {
        return null;
    }

    @Transactional
    @Override
    public User deactivateUser(User user) throws BusinessException {
        return null;
    }

    @Override
    public void verifyEmail(String token) throws BusinessException {

    }

    protected PageRequest toPageRequest(Paging paging) {
        Sort sortBy = Sort.by(Sort.Direction.ASC,"id");
        if(Objects.nonNull(paging.getSortingDirection()) && Objects.nonNull(paging.getSortingColumn())){
            sortBy = Sort.by(Sort.Direction.valueOf(paging.getSortingDirection()), paging.getSortingColumn());
        }
        return PageRequest.of(paging.getPagingStart(), paging.getPagingSize(), sortBy);
    }


    public String translate(String key) {
        return Optional.ofNullable(messageSource.getMessage(key, null, getDefaultLocale())).orElse(key);
    }


    public String translate(String key, Locale locale) {
        return Optional.ofNullable(messageSource.getMessage(key, null, locale)).orElse(key);
    }

    protected Locale getDefaultLocale() {
        return Locale.ENGLISH;
    }
}
