package com.pm.authservice.service;

import com.pm.authservice.dto.CreateUserDTO;
import com.pm.authservice.dto.UpdateUserDTO;
import com.pm.authservice.dto.UserDTO;
import com.pm.authservice.dto.UsersSearchRequestDTO;
import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.exception.NotFoundException;
import com.pm.authservice.model.UserEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    public UserDTO convertToDTO(UserEntity user, Boolean withRoles);
    public UserEntity convertToEntity(UserDTO dto);
    public List<UserDTO> convertToDTOList(List<UserEntity> userList, Boolean withRoles);
    public UserEntity findById(Integer id) throws NotFoundException;
    public UserEntity findByPublicId(String publicId)throws NotFoundException;
    public UserEntity findByEmail(String email)throws NotFoundException;
    public void deleteUser(String publicId) throws NotFoundException;
    public Page<UserEntity> searchUsers(UsersSearchRequestDTO searchObj);
    public Long countUsers(UsersSearchRequestDTO searchObj);
    public List<UserDTO> findAllUsersForExport(UsersSearchRequestDTO searchObj);
    public UserEntity registerUser(CreateUserDTO dto, String applicationUrl) throws BusinessException;
    public UserEntity updateUser(String publicId, UpdateUserDTO dto) throws NotFoundException;
    public UserEntity activateUser(UserEntity user) throws BusinessException;
    public UserEntity deactivateUser(UserEntity user) throws BusinessException;
    public void verifyEmail(String token) throws BusinessException;
    public void validatePasswordChange(String newPassword, String confirmPassword, boolean isPasswordRequired)
            throws BusinessException;



}
