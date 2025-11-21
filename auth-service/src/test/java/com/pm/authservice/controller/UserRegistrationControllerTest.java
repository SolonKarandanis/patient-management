package com.pm.authservice.controller;

import com.pm.authservice.user.controller.UserRegistrationController;
import com.pm.authservice.user.dto.CreateUserDTO;
import com.pm.authservice.user.dto.UserDTO;
import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.user.model.UserEntity;
import com.pm.authservice.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import util.TestUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("UserRegistrationControllerTest")
@ExtendWith(MockitoExtension.class)
public class UserRegistrationControllerTest {

    @InjectMocks
    protected UserRegistrationController controller;

    @Mock
    protected UserService userService;

    @Mock
    protected HttpServletRequest request;

    protected UserEntity user;
    protected UserDTO userDto;
    protected final Integer userId = 1;

    @BeforeEach
    public void setup(){
        user = TestUtil.createTestUser(userId);
        userDto = TestUtil.createTestUserDto(userId);
    }

    @DisplayName("Register a user")
    @Test
    void testRegisterUser() throws BusinessException {
        CreateUserDTO createUserRequest = TestUtil.createTestCreateUserDTO();
        when(userService.registerUser(createUserRequest,"http://null:0null")).thenReturn(user);
        when(userService.convertToDTO(user,true)).thenReturn(userDto);

        ResponseEntity<UserDTO> resp = controller.registerUser(createUserRequest,request);
        assertNotNull(resp);
        assertNotNull(resp.getBody());
        assertEquals(resp.getBody(), userDto);
        assertTrue(resp.getStatusCode().isSameCodeAs(HttpStatus.OK));

        verify(userService,times(1)).registerUser(createUserRequest,"http://null:0null");
        verify(userService, times(1)).convertToDTO(user,true);
    }

}
