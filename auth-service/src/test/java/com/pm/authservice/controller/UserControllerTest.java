package com.pm.authservice.controller;

import com.pm.authservice.auth.UserDetailsDTO;
import com.pm.authservice.user.dto.ChangePasswordDTO;
import com.pm.authservice.dto.SearchResults;
import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.user.controller.UserController;
import com.pm.authservice.user.dto.*;
import com.pm.authservice.user.model.UserEntity;
import com.pm.authservice.user.service.UserService;
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
import util.TestConstants;
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

    @DisplayName("View User")
    @Test
    void testViewUser(){
        when(userService.findByPublicId(TestConstants.TEST_USER_PUBLIC_ID)).thenReturn(user);
        when(userService.convertToDTO(user,true)).thenReturn(userDto);

        ResponseEntity<UserDTO> resp = controller.viewUser(TestConstants.TEST_USER_PUBLIC_ID);
        assertNotNull(resp);
        assertNotNull(resp.getBody());
        assertEquals(resp.getBody(), userDto);
        assertTrue(resp.getStatusCode().isSameCodeAs(HttpStatus.OK));

        verify(userService, times(1)).findByPublicId(TestConstants.TEST_USER_PUBLIC_ID);
        verify(userService, times(1)).convertToDTO(user,true);
    }

    @DisplayName("Get user by token")
    @Test
    void testGetUserByToken(){
        when(userService.findByPublicId(detailsDTO.getPublicId())).thenReturn(user);
        when(userService.convertToDTO(user,true)).thenReturn(userDto);

        ResponseEntity<UserDTO> resp = controller.getUserByToken(authentication);
        assertNotNull(resp);
        assertNotNull(resp.getBody());
        assertEquals(resp.getBody(), userDto);
        assertTrue(resp.getStatusCode().isSameCodeAs(HttpStatus.OK));

        verify(userService, times(1)).findByPublicId(TestConstants.TEST_USER_PUBLIC_ID);
        verify(userService, times(1)).convertToDTO(user,true);
    }

    @DisplayName("Update user")
    @Test
    void testUpdateUser(){
        UpdateUserDTO dto = new UpdateUserDTO();
        when(userService.updateUser(TestConstants.TEST_USER_PUBLIC_ID,dto)).thenReturn(user);
        when(userService.convertToDTO(user,true)).thenReturn(userDto);

        ResponseEntity<UserDTO> resp = controller.updateUser(TestConstants.TEST_USER_PUBLIC_ID,dto);
        assertNotNull(resp);
        assertNotNull(resp.getBody());
        assertEquals(resp.getBody(), userDto);
        assertTrue(resp.getStatusCode().isSameCodeAs(HttpStatus.OK));

        verify(userService, times(1)).updateUser(TestConstants.TEST_USER_PUBLIC_ID,dto);
        verify(userService, times(1)).convertToDTO(user,true);
    }

    @DisplayName("Delete user")
    @Test
    void testDeleteUser(){
        doNothing().when(userService).deleteUser(TestConstants.TEST_USER_PUBLIC_ID);

        ResponseEntity<Void> resp = controller.deleteUser(TestConstants.TEST_USER_PUBLIC_ID);
        assertNotNull(resp);
        assertNull(resp.getBody());
        assertTrue(resp.getStatusCode().isSameCodeAs(HttpStatus.NO_CONTENT));

        verify(userService, times(1)).deleteUser(TestConstants.TEST_USER_PUBLIC_ID);
    }

    @DisplayName("Activate User")
    @Test
    void testActivateUser() throws BusinessException {
        when(userService.findByPublicId(TestConstants.TEST_USER_PUBLIC_ID)).thenReturn(user);
        when(userService.activateUser(user)).thenReturn(user);
        when(userService.convertToDTO(user,true)).thenReturn(userDto);

        ResponseEntity<UserDTO> resp = controller.activateUser(TestConstants.TEST_USER_PUBLIC_ID);
        assertNotNull(resp);
        assertNotNull(resp.getBody());
        assertEquals(resp.getBody(), userDto);
        assertTrue(resp.getStatusCode().isSameCodeAs(HttpStatus.OK));

        verify(userService, times(1)).findByPublicId(TestConstants.TEST_USER_PUBLIC_ID);
        verify(userService, times(1)).activateUser(user);
        verify(userService, times(1)).convertToDTO(user,true);
    }

    @DisplayName("Deactivate User")
    @Test
    void testDeactivateUser() throws BusinessException{
        when(userService.findByPublicId(TestConstants.TEST_USER_PUBLIC_ID)).thenReturn(user);
        when(userService.deactivateUser(user)).thenReturn(user);
        when(userService.convertToDTO(user,true)).thenReturn(userDto);

        ResponseEntity<UserDTO> resp = controller.deactivateUser(TestConstants.TEST_USER_PUBLIC_ID);
        assertNotNull(resp);
        assertNotNull(resp.getBody());
        assertEquals(resp.getBody(), userDto);
        assertTrue(resp.getStatusCode().isSameCodeAs(HttpStatus.OK));

        verify(userService, times(1)).findByPublicId(TestConstants.TEST_USER_PUBLIC_ID);
        verify(userService, times(1)).deactivateUser(user);
        verify(userService, times(1)).convertToDTO(user,true);
    }

    @DisplayName("Verify Email")
    @Test
    void testVerifyEmail() throws BusinessException {
        String token = "test_token";
        doNothing().when(userService).verifyEmail(token);

        ResponseEntity<Void> resp = controller.verifyEmail(token);
        assertNotNull(resp);
        assertNull(resp.getBody());
        assertTrue(resp.getStatusCode().isSameCodeAs(HttpStatus.NO_CONTENT));

        verify(userService,times(1)).verifyEmail(token);
    }

    @DisplayName("Change Password")
    @Test
    void testChangePassword() throws BusinessException {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        when(userService.findByPublicId(TestConstants.TEST_USER_PUBLIC_ID)).thenReturn(user);
        when(userService.changePassword(user,dto)).thenReturn(user);
        when(userService.convertToDTO(user,true)).thenReturn(userDto);

        ResponseEntity<UserDTO> resp = controller.changeUserPassword(TestConstants.TEST_USER_PUBLIC_ID,dto);
        assertNotNull(resp);
        assertNotNull(resp.getBody());
        assertEquals(resp.getBody(), userDto);
        assertTrue(resp.getStatusCode().isSameCodeAs(HttpStatus.OK));

        verify(userService, times(1)).findByPublicId(TestConstants.TEST_USER_PUBLIC_ID);
        verify(userService, times(1)).changePassword(user,dto);
        verify(userService, times(1)).convertToDTO(user,true);
    }
}
