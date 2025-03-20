package com.pm.authservice.service;

import com.pm.authservice.dto.CreateUserDTO;
import com.pm.authservice.dto.UpdateUserDTO;
import com.pm.authservice.dto.UserDTO;
import com.pm.authservice.dto.UsersSearchRequestDTO;
import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.exception.NotFoundException;
import com.pm.authservice.model.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface UserService {
    public UserDTO convertToDTO(User user);
    public User convertToEntity(UserDTO dto);
    public List<UserDTO> convertToDTOList(List<User> userList);
    public User findById(Integer id) throws NotFoundException;
    public User findByPublicId(String publicId)throws NotFoundException;
    public User findByEmail(String email)throws NotFoundException;
    public void deleteUser(String publicId) throws NotFoundException;
    public Page<User> searchUsers(UsersSearchRequestDTO searchObj);
    public Long countUsers(UsersSearchRequestDTO searchObj);
    public List<User> findAllUsersForExport(UsersSearchRequestDTO searchObj);
    public User registerUser(CreateUserDTO dto) throws BusinessException;
    public User updateUser(String publicId, UpdateUserDTO dto) throws NotFoundException;
    public User activateUser(User user) throws BusinessException;
    public User deactivateUser(User user) throws BusinessException;
    public void verifyEmail(String token) throws BusinessException;



}
