package com.pm.authservice.controller;

import com.pm.authservice.infrastructure.web.dto.UserDetailsDTO;
import com.pm.authservice.infrastructure.web.dto.ChangePasswordDTO;
import com.pm.authservice.infrastructure.web.dto.SearchResults;
import com.pm.authservice.infrastructure.web.exception.BusinessException;
import com.pm.authservice.infrastructure.web.controller.UserController;
import com.pm.authservice.infrastructure.web.dto.*;
import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import com.pm.authservice.infrastructure.search.SearchService;
import com.pm.authservice.user.service.UserLifecycleService;
import com.pm.authservice.user.service.UserQueryService;
import com.pm.authservice.infrastructure.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import util.TestConstants;
import util.TestUtil;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    protected UserJpaRepository userRepository;
    @Mock
    protected UserLifecycleService lifecycleService;
    @Mock
    protected SearchService searchService;
    @Mock
    protected UserQueryService userQueryService;

    protected Authentication authentication;
    protected MockHttpServletResponse response;
    protected final Integer userId = 1;
    protected UserJpaEntity user;
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

    @DisplayName("Export to csv (max results)")
    @Test
    void testExportUsersToCsv() throws Exception {
        UsersSearchRequestDTO searchObj = TestUtil.generateUsersSearchRequestDTO();
        when(userRepository.findByDomainId(UUID.fromString(detailsDTO.getPublicId()))).thenReturn(Optional.of(user));
        when(searchService.countUsers(searchObj, user)).thenReturn(11000L);

        BusinessException exception = assertThrows(BusinessException.class,()->{
            controller.exportUsersToCsv(searchObj,response,authentication);
        });
        assertEquals("error.max.csv.results",exception.getLocalizedMessage());

        verify(userRepository,times(1)).findByDomainId(UUID.fromString(detailsDTO.getPublicId()));
        verify(searchService,times(1)).countUsers(searchObj, user);
    }

    @DisplayName("Export to csv")
    @Test
    void testExportUsersToCsv02() throws Exception {
        response.setOutputStreamAccessAllowed(true);
        UsersSearchRequestDTO searchObj = TestUtil.generateUsersSearchRequestDTO();

        when(userRepository.findByDomainId(UUID.fromString(detailsDTO.getPublicId()))).thenReturn(Optional.of(user));
        when(searchService.countUsers(searchObj, user)).thenReturn(500L);
        when(searchService.findUsersForExport(searchObj, user)).thenReturn(dtos);

        controller.exportUsersToCsv(searchObj, response, authentication);

        verify(userRepository,times(1)).findByDomainId(UUID.fromString(detailsDTO.getPublicId()));
        verify(searchService,times(1)).countUsers(searchObj, user);
        verify(searchService,times(1)).findUsersForExport(searchObj, user);
    }

    @DisplayName("Find All Users")
    @Test
    void testFindAllUsers() throws Exception {
        UsersSearchRequestDTO searchObj = TestUtil.generateUsersSearchRequestDTO();
        SearchResults<UserDTO> searchResults = new SearchResults<>(dtos.size(), dtos);
        when(userRepository.findByDomainId(UUID.fromString(detailsDTO.getPublicId()))).thenReturn(Optional.of(user));
        when(searchService.advancedSearchUsers(searchObj, user)).thenReturn(searchResults);

        ResponseEntity<SearchResults<UserDTO>> resp = controller.findAllUsers(searchObj, authentication);
        assertNotNull(resp);
        assertNotNull(resp.getBody());
        assertEquals(resp.getBody().getList(), dtos);
        assertTrue(resp.getStatusCode().isSameCodeAs(HttpStatus.OK));

        verify(userRepository,times(1)).findByDomainId(UUID.fromString(detailsDTO.getPublicId()));
        verify(searchService, times(1)).advancedSearchUsers(searchObj, user);
    }

    @DisplayName("View User")
    @Test
    void testViewUser(){
        when(userQueryService.viewUser(TestConstants.TEST_USER_PUBLIC_ID)).thenReturn(userDto);

        ResponseEntity<UserDTO> resp = controller.viewUser(TestConstants.TEST_USER_PUBLIC_ID);
        assertNotNull(resp);
        assertNotNull(resp.getBody());
        assertEquals(resp.getBody(), userDto);
        assertTrue(resp.getStatusCode().isSameCodeAs(HttpStatus.OK));

        verify(userQueryService, times(1)).viewUser(TestConstants.TEST_USER_PUBLIC_ID);
    }

    @DisplayName("Get user by token")
    @Test
    void testGetUserByToken(){
        when(userQueryService.viewUser(detailsDTO.getPublicId())).thenReturn(userDto);

        ResponseEntity<UserDTO> resp = controller.getUserByToken(authentication);
        assertNotNull(resp);
        assertNotNull(resp.getBody());
        assertEquals(resp.getBody(), userDto);
        assertTrue(resp.getStatusCode().isSameCodeAs(HttpStatus.OK));

        verify(userQueryService, times(1)).viewUser(TestConstants.TEST_USER_PUBLIC_ID);
    }

    @DisplayName("Update user")
    @Test
    void testUpdateUser(){
        UpdateUserDTO dto = new UpdateUserDTO();
        when(lifecycleService.updateUser(TestConstants.TEST_USER_PUBLIC_ID, dto)).thenReturn(userDto);

        ResponseEntity<UserDTO> resp = controller.updateUser(TestConstants.TEST_USER_PUBLIC_ID, dto);
        assertNotNull(resp);
        assertNotNull(resp.getBody());
        assertEquals(resp.getBody(), userDto);
        assertTrue(resp.getStatusCode().isSameCodeAs(HttpStatus.OK));

        verify(lifecycleService, times(1)).updateUser(TestConstants.TEST_USER_PUBLIC_ID, dto);
    }

    @DisplayName("Delete user")
    @Test
    void testDeleteUser(){
        doNothing().when(lifecycleService).deleteUser(TestConstants.TEST_USER_PUBLIC_ID);

        ResponseEntity<Void> resp = controller.deleteUser(TestConstants.TEST_USER_PUBLIC_ID);
        assertNotNull(resp);
        assertNull(resp.getBody());
        assertTrue(resp.getStatusCode().isSameCodeAs(HttpStatus.NO_CONTENT));

        verify(lifecycleService, times(1)).deleteUser(TestConstants.TEST_USER_PUBLIC_ID);
    }

    @DisplayName("Activate User")
    @Test
    void testActivateUser() throws BusinessException {
        when(lifecycleService.activateUser(TestConstants.TEST_USER_PUBLIC_ID)).thenReturn(userDto);

        ResponseEntity<UserDTO> resp = controller.activateUser(TestConstants.TEST_USER_PUBLIC_ID);
        assertNotNull(resp);
        assertNotNull(resp.getBody());
        assertEquals(resp.getBody(), userDto);
        assertTrue(resp.getStatusCode().isSameCodeAs(HttpStatus.OK));

        verify(lifecycleService, times(1)).activateUser(TestConstants.TEST_USER_PUBLIC_ID);
    }

    @DisplayName("Deactivate User")
    @Test
    void testDeactivateUser() throws BusinessException {
        when(lifecycleService.deactivateUser(TestConstants.TEST_USER_PUBLIC_ID)).thenReturn(userDto);

        ResponseEntity<UserDTO> resp = controller.deactivateUser(TestConstants.TEST_USER_PUBLIC_ID);
        assertNotNull(resp);
        assertNotNull(resp.getBody());
        assertEquals(resp.getBody(), userDto);
        assertTrue(resp.getStatusCode().isSameCodeAs(HttpStatus.OK));

        verify(lifecycleService, times(1)).deactivateUser(TestConstants.TEST_USER_PUBLIC_ID);
    }

    @DisplayName("Change Password")
    @Test
    void testChangePassword() throws BusinessException {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        when(lifecycleService.changePassword(TestConstants.TEST_USER_PUBLIC_ID, dto)).thenReturn(userDto);

        ResponseEntity<UserDTO> resp = controller.changeUserPassword(TestConstants.TEST_USER_PUBLIC_ID, dto);
        assertNotNull(resp);
        assertNotNull(resp.getBody());
        assertEquals(resp.getBody(), userDto);
        assertTrue(resp.getStatusCode().isSameCodeAs(HttpStatus.OK));

        verify(lifecycleService, times(1)).changePassword(TestConstants.TEST_USER_PUBLIC_ID, dto);
    }
}
