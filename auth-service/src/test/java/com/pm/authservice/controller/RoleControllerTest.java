package com.pm.authservice.controller;

import com.pm.authservice.infrastructure.web.dto.RoleDTO;
import com.pm.authservice.infrastructure.web.controller.RoleController;
import com.pm.authservice.infrastructure.application.RoleQueryService;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("RoleControllerTest")
@ExtendWith(MockitoExtension.class)
public class RoleControllerTest {

    @InjectMocks
    protected RoleController controller;

    @Mock
    protected RoleQueryService roleQueryService;

    protected RoleDTO roleDto;

    @BeforeEach
    public void setup(){
        roleDto = TestUtil.createTestRoleDTO();
    }

    @DisplayName("Fetch all roles")
    @Test
    void testFindAllRoles(){
        List<RoleDTO> roleDTOS = List.of(roleDto);
        when(roleQueryService.findAllRoles()).thenReturn(roleDTOS);

        ResponseEntity<List<RoleDTO>> resp = controller.findAllRoles();
        assertNotNull(resp);
        assertNotNull(resp.getBody());
        assertEquals(resp.getBody(), roleDTOS);
        assertTrue(resp.getStatusCode().isSameCodeAs(HttpStatus.OK));

        verify(roleQueryService, times(1)).findAllRoles();
    }
}
