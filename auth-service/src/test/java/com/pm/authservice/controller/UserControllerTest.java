package com.pm.authservice.controller;

import com.pm.authservice.dto.SearchResults;
import com.pm.authservice.dto.UserDTO;
import com.pm.authservice.dto.UserDetailsDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import util.TestUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@DisplayName("UserControllerTest")
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    protected UserController controller;

    @Mock
    protected UserService userService;
    @Mock
    protected Page<UserEntity> results;

    protected Authentication authentication;
    protected MockHttpServletResponse response;
    protected final Integer userId = 1;
    protected UserEntity user;
    protected UserDTO userDto;
    protected UserDetailsDTO detailsDTO;
    protected List<UserDTO> dtos;

    @BeforeEach
    public void setup(){
        response = new MockHttpServletResponse();
        user = TestUtil.createTestUser(userId);
        userDto = TestUtil.createTestUserDto(userId);
        detailsDTO = TestUtil.createTestUserDetailsDTO(userId);
        authentication = TestUtil.getTestAuthenticationFromUserDTO(detailsDTO);
        dtos = List.of(userDto);
    }

    @DisplayName("Export to csv ( max results)")
    @Test
    void testExportUsersToCsv() throws BusinessException{
        UsersSearchRequestDTO searchObj = TestUtil.generateUsersSearchRequestDTO();
        when(userService.findByPublicId(detailsDTO.getPublicId())).thenReturn(user);
        when(userService.countUsers(searchObj, user)).thenReturn(11000L);

        BusinessException exception = assertThrows(BusinessException.class,()->{
            controller.exportUsersToCsv(searchObj,response,authentication);
        });
        assertEquals("error.max.csv.results",exception.getLocalizedMessage());

        verify(userService,times(1)).findByPublicId(detailsDTO.getPublicId());
        verify(userService,times(1)).countUsers(searchObj, user);
    }

    @DisplayName("Export to csv")
    @Test
    void testExportUsersToCsv02() throws Exception{
        response.setOutputStreamAccessAllowed(true);
        UsersSearchRequestDTO searchObj = TestUtil.generateUsersSearchRequestDTO();

        when(userService.findByPublicId(detailsDTO.getPublicId())).thenReturn(user);
        when(userService.countUsers(searchObj, user)).thenReturn(500L);
        when(userService.findAllUsersForExport(searchObj,user)).thenReturn(dtos);

        controller.exportUsersToCsv(searchObj,response, authentication);

        verify(userService,times(1)).findByPublicId(detailsDTO.getPublicId());
        verify(userService,times(1)).countUsers(searchObj, user);
        verify(userService,times(1)).findAllUsersForExport(searchObj,user);
    }

    @DisplayName("Find All Users")
    @Test
    void testFindAllUsers(){
        UsersSearchRequestDTO searchObj = TestUtil.generateUsersSearchRequestDTO();
        when(userService.findByPublicId(detailsDTO.getPublicId())).thenReturn(user);
        when(userService.searchUsers(searchObj,user)).thenReturn(results);
        when(userService.convertToDTOList(results.getContent(),false)).thenReturn(dtos);

        ResponseEntity<SearchResults<UserDTO>> resp = controller.findAllUsers(searchObj,authentication);
        assertNotNull(resp);
        assertNotNull(resp.getBody());
        assertEquals(resp.getBody().getCountRows(),Math.toIntExact(results.getTotalElements()));
        assertEquals(resp.getBody().getList(),dtos);
        assertTrue(resp.getStatusCode().isSameCodeAs(HttpStatus.OK));

        verify(userService,times(1)).findByPublicId(detailsDTO.getPublicId());
        verify(userService, times(1)).searchUsers(searchObj,user);
        verify(userService, times(1)).convertToDTOList(results.getContent(),false);
    }
}
