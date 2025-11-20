package com.pm.authservice.user.service;

import com.pm.authservice.dto.RoleDTO;
import com.pm.authservice.exception.NotFoundException;
import com.pm.authservice.service.RoleService;
import com.pm.authservice.service.VerificationTokenService;
import com.pm.authservice.user.dto.UserDTO;
import com.pm.authservice.user.dto.UserDetailsDTO;
import com.pm.authservice.user.model.UserEntity;
import com.pm.authservice.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import util.TestConstants;
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

    @Mock
    protected ApplicationEventPublisher publisher;

    protected final Integer userId = 1;
    protected UserEntity user;
    protected UserDTO userDto;
    protected UserDetailsDTO detailsDTO;
    protected List<UserDTO> dtos;
    protected List<RoleDTO> roleDtos;

    @BeforeEach
    public void setup(){
        service.setPublisher(publisher);
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

    @DisplayName("Find a user by id")
    @Test
    void testFindById(){
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserEntity userEntity = service.findById(userId);
        assertNotNull(userEntity);

        verify(userRepository,times(1)).findById(userId);
    }

    @DisplayName("Find a user by id (Not Found) ")
    @Test
    void testFindById02(){
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception =assertThrows(NotFoundException.class,()->{
            service.findById(userId);
        });
        assertEquals("error.user.not.found",exception.getLocalizedMessage());
    }

    @DisplayName("Find a user by public id")
    @Test
    void testFindByPublicId(){
        when(userRepository.findByPublicId(UUID.fromString(TestConstants.TEST_USER_PUBLIC_ID))).thenReturn(Optional.of(user));

        UserEntity userEntity = service.findByPublicId(TestConstants.TEST_USER_PUBLIC_ID);
        assertNotNull(userEntity);

        verify(userRepository,times(1)).findByPublicId(UUID.fromString(TestConstants.TEST_USER_PUBLIC_ID));
    }

    @DisplayName("Find a user by public id (Not Found) ")
    @Test
    void testFindByPublicId02(){
        when(userRepository.findByPublicId(UUID.fromString(userDto.getPublicId()))).thenReturn(Optional.empty());

        NotFoundException exception =assertThrows(NotFoundException.class,()->{
            service.findByPublicId(TestConstants.TEST_USER_PUBLIC_ID);
        });
        assertEquals("error.user.not.found",exception.getLocalizedMessage());

        verify(userRepository,times(1)).findByPublicId(UUID.fromString(TestConstants.TEST_USER_PUBLIC_ID));
    }

    @DisplayName("Find a user by email")
    @Test
    void testFindByEmail(){
        when(userRepository.findByEmail(TestConstants.TEST_USER_EMAIL)).thenReturn(Optional.of(user));

        UserEntity userEntity = service.findByEmail(TestConstants.TEST_USER_EMAIL);
        assertNotNull(userEntity);

        verify(userRepository,times(1)).findByEmail(TestConstants.TEST_USER_EMAIL);
    }

    @DisplayName("Find a user by email (Not Found) ")
    @Test
    void testFindByEmail02(){
        when(userRepository.findByEmail(TestConstants.TEST_USER_EMAIL)).thenReturn(Optional.empty());

        NotFoundException exception =assertThrows(NotFoundException.class,()->{
            service.findByEmail(TestConstants.TEST_USER_EMAIL);
        });
        assertEquals("error.user.not.found",exception.getLocalizedMessage());

        verify(userRepository,times(1)).findByEmail(TestConstants.TEST_USER_EMAIL);
    }

    @DisplayName("Delete a user")
    @Test
    void testDeleteUser(){
        when(userRepository.findByPublicId(UUID.fromString(TestConstants.TEST_USER_PUBLIC_ID))).thenReturn(Optional.of(user));

        service.deleteUser(TestConstants.TEST_USER_PUBLIC_ID);

        verify(userRepository,times(1)).findByPublicId(UUID.fromString(TestConstants.TEST_USER_PUBLIC_ID));
        verify(userRepository,times(1)).delete(user);
    }
}
