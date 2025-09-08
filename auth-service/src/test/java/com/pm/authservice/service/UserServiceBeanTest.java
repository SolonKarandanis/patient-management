package com.pm.authservice.service;

import com.pm.authservice.dto.RoleDTO;
import com.pm.authservice.dto.UserDTO;
import com.pm.authservice.dto.UserDetailsDTO;
import com.pm.authservice.model.UserEntity;
import com.pm.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import util.TestUtil;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("UserServiceBeanTest")
@ExtendWith(MockitoExtension.class)
public class UserServiceBeanTest {

    @InjectMocks
    protected UserServiceBean service;

    @Mock
    protected UserRepository userRepository;

    @Mock
    protected RoleService roleService;

    @Mock
    protected VerificationTokenService verificationTokenService;

    @Mock
    protected PasswordEncoder passwordEncoder;

    protected final Integer userId = 1;
    protected UserEntity user;
    protected UserDTO userDto;
    protected UserDetailsDTO detailsDTO;
    protected List<UserDTO> dtos;
    protected List<RoleDTO> roleDtos;

    @BeforeEach
    public void setup(){
        user = TestUtil.createTestUser(userId);
        userDto = TestUtil.createTestUserDto(userId);
        detailsDTO = TestUtil.createTestUserDetailsDTO(userId);
        dtos = List.of(userDto);
        roleDtos= List.of(TestUtil.createTestRoleDTO());
    }

    @DisplayName("Convert to DTO without roles")
    @Test
    void testConvertToDTO(){
        UserDTO userDto =service.convertToDTO(user,false);
        assertNotNull(userDto);
        assertNull(userDto.getRoles());
    }

    @DisplayName("Convert to DTO with roles")
    @Test
    void testConvertToDTO02(){
        when(roleService.convertToDtoList(user.getRoles())).thenReturn(roleDtos);

        UserDTO userDto =service.convertToDTO(user,true);
        assertNotNull(userDto);
        assertNotNull(userDto.getRoles());
        assertEquals(userDto.getRoles().size(),roleDtos.size());

        verify(roleService,times(1)).convertToDtoList(user.getRoles());
    }

    @DisplayName("Convert to Entity without roles and without publicId")
    @Test
    void testConvertToEntity(){
        userDto.setPublicId(null);
        userDto.setRoles(new ArrayList<>());

        UserEntity userEntity = service.convertToEntity(userDto);
        assertNotNull(userEntity);
        assertTrue(userEntity.getRoles().isEmpty());
        assertNull(userEntity.getPublicId());
    }

    @DisplayName("Convert to Entity with roles and with publicId")
    @Test
    void testConvertToEntity02(){
        when(userRepository.findIdByPublicId(UUID.fromString(userDto.getPublicId()))).thenReturn(Optional.of(userId));
        when(roleService.findByIds(List.of(1))).thenReturn(List.of(TestUtil.createTestRole()));

        UserEntity userEntity = service.convertToEntity(userDto);
        assertNotNull(userEntity);
        assertNotNull(userEntity.getPublicId());
        assertEquals(userEntity.getRoles().size(),roleDtos.size());

        verify(userRepository,times(1)).findIdByPublicId(UUID.fromString(userDto.getPublicId()));
        verify(roleService,times(1)).findByIds(List.of(1));
    }

    @DisplayName("Convert to DTO list with empty array")
    @Test
    void testConvertToDTOList(){

        List<UserDTO> userDtos = service.convertToDTOList( Collections.emptyList(),true);
        assertNotNull(userDtos);
        assertEquals(0, userDtos.size());
    }

    @DisplayName("Convert to DTO list")
    @Test
    void testConvertToDTOList02(){
        List<UserDTO> userDtos = service.convertToDTOList( List.of(user),true);
        assertNotNull(userDtos);
        assertEquals(1, userDtos.size());
    }
}
