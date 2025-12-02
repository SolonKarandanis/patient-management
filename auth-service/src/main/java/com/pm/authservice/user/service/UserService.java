package com.pm.authservice.user.service;

import com.pm.authservice.user.dto.ChangePasswordDTO;
import com.pm.authservice.user.dto.*;
import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.exception.NotFoundException;
import com.pm.authservice.user.model.UserEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    UserDTO convertToDTO(UserEntity user, Boolean withRoles);
    UserEntity convertToEntity(UserDTO dto);
    List<UserDTO> convertToDTOList(List<UserEntity> userList, Boolean withRoles);
    UserEntity findById(Integer id) throws NotFoundException;
    UserEntity findByPublicId(String publicId)throws NotFoundException;
    List<String> getUserPermissions(String publicId)throws NotFoundException;
    UserEntity findByEmail(String email)throws NotFoundException;
    void deleteUser(String publicId) throws NotFoundException;
    Page<UserEntity> searchUsers(UsersSearchRequestDTO searchObj, UserEntity loggedUser);
    Long countUsers(UsersSearchRequestDTO searchObj,UserEntity loggedUser);
    List<UserDTO> findAllUsersForExport(UsersSearchRequestDTO searchObj, UserEntity loggedUser);
    UserEntity registerUser(CreateUserDTO dto, String applicationUrl) throws BusinessException;
    UserEntity updateUser(String publicId, UpdateUserDTO dto) throws NotFoundException;
    UserEntity activateUser(UserEntity user) throws BusinessException;
    UserEntity deactivateUser(UserEntity user) throws BusinessException;
    void verifyEmail(String token) throws BusinessException;
    void validatePasswordChange(String newPassword, String confirmPassword, boolean isPasswordRequired)
            throws BusinessException;
    UserEntity changePassword(UserEntity user, ChangePasswordDTO dto) throws BusinessException;;


}
