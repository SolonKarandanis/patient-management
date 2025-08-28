package com.pm.authservice.controller;

import com.pm.authservice.dto.UserDTO;
import com.pm.authservice.dto.UsersSearchRequestDTO;
import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.model.UserEntity;
import com.pm.authservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import util.TestUtil;

import java.util.List;

import static org.mockito.Mockito.when;

@DisplayName("UserControllerTest")
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    protected UserController userController;

    @Mock
    protected UserService userService;
    protected MockHttpServletResponse response;
    protected final Integer userId = 1;
    protected UserEntity user;
    protected UserDTO userDto;
    protected List<UserDTO> dtos;

    @BeforeEach
    public void setup(){
        response = new MockHttpServletResponse();
        user = TestUtil.createTestUser(userId);
        userDto = TestUtil.createTestUserDto(userId);
        dtos = List.of(userDto);
    }

    @DisplayName("Export to csv ( max results)")
    @Test
    void testExportUsersToCsv() throws BusinessException{
        UsersSearchRequestDTO searchObj = TestUtil.generateUsersSearchRequestDTO();
        when(userService.countUsers(searchObj, user)).thenReturn(11000L);
    }
}
