package com.pm.authservice.controller;

import com.pm.authservice.user.dto.RoleDTO;
import com.pm.authservice.user.controller.RoleController;
import com.pm.authservice.user.model.RoleEntity;
import com.pm.authservice.user.service.RoleService;
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

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("RoleControllerTest")
@ExtendWith(MockitoExtension.class)
public class RoleControllerTest {

    @InjectMocks
    protected RoleController controller;

    @Mock
    protected RoleService roleService;

    protected RoleEntity role;

    protected RoleDTO roleDto;

    @BeforeEach
    public void setup(){
        role = TestUtil.createTestRole();
        roleDto = TestUtil.createTestRoleDTO();
    }

    @DisplayName("Fetch all roles")
    @Test
    void testFindAllRoles(){
        List<RoleEntity> roles = List.of(role);
        List<RoleDTO> roleDTOS = List.of(roleDto);
        when(roleService.findAll()).thenReturn(roles);
        when(roleService.convertToDtoList(new HashSet<>(roles))).thenReturn(roleDTOS);

        ResponseEntity<List<RoleDTO>> resp = controller.findAllRoles();
        assertNotNull(resp);
        assertNotNull(resp.getBody());
        assertEquals(resp.getBody(), roleDTOS);
        assertTrue(resp.getStatusCode().isSameCodeAs(HttpStatus.OK));

        verify(roleService,times(1)).findAll();
        verify(roleService,times(1)).convertToDtoList(new HashSet<>(roles));
    }
}
