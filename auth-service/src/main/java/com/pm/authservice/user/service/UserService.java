package com.pm.authservice.user.service;

import com.pm.authservice.user.dto.ChangePasswordDTO;
import com.pm.authservice.user.dto.*;
import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.exception.NotFoundException;
import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface UserService {
    UserDTO convertToDTO(UserJpaEntity user, Boolean withRoles);
    UserJpaEntity convertToEntity(UserDTO dto);
    List<UserDTO> convertToDTOList(List<UserJpaEntity> userList, Boolean withRoles);
    UserJpaEntity findById(Integer id) throws NotFoundException;
    UserJpaEntity findByPublicId(String publicId)throws NotFoundException;
    List<String> getUserPermissions(String publicId)throws NotFoundException;
    UserJpaEntity findByEmail(String email)throws NotFoundException;
    List<UserJpaEntity> findUsersToBeIndexedByIdRange(Integer minId, Integer maxId);
    MinMaxUserIdDTO getMinAndMaxUserId();
    void deleteUser(String publicId) throws NotFoundException;
    Page<UserJpaEntity> searchUsers(UsersSearchRequestDTO searchObj, UserJpaEntity loggedUser);
    Page<UserJpaEntity> quickSearchUsers(String quickSearchValueParam, PageRequest pageRequest, UserJpaEntity loggedUser);
    Long countUsers(UsersSearchRequestDTO searchObj,UserJpaEntity loggedUser);
    List<UserDTO> findAllUsersForExport(UsersSearchRequestDTO searchObj, UserJpaEntity loggedUser);
    UserJpaEntity registerUser(CreateUserDTO dto, String applicationUrl) throws BusinessException;
    UserJpaEntity updateUser(String publicId, UpdateUserDTO dto) throws NotFoundException;
    UserJpaEntity activateUser(UserJpaEntity user) throws BusinessException;
    UserJpaEntity deactivateUser(UserJpaEntity user) throws BusinessException;
    void validatePasswordChange(String newPassword, String confirmPassword, boolean isPasswordRequired)
            throws BusinessException;
    UserJpaEntity changePassword(UserJpaEntity user, ChangePasswordDTO dto) throws BusinessException;;


}
