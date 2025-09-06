package com.pm.authservice.controller;

import com.pm.authservice.dto.UserDetailsDTO;
import com.pm.authservice.service.AuthService;
import com.pm.authservice.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import util.TestConstants;
import util.TestUtil;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    protected AuthService authService;

    @MockitoBean
    protected JwtService jwtService;

    protected final Integer userId = 1;
    protected Authentication authentication;
    protected UserDetailsDTO detailsDTO;

    @BeforeEach
    void setUp() {
        detailsDTO = TestUtil.createTestUserDetailsDTO(userId);
        authentication = TestUtil.getTestAuthenticationFromUserDTO(detailsDTO);
    }

    @DisplayName("Test token validity (Token valid)")
    @Test
    void testValidateToken() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("Authorization", TestConstants.TEST_TOKEN);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAll(map);

        when(jwtService.extractUsername(anyString())).thenReturn("testuser");
//        when(jwtService.isTokenValid(anyString(), any(UserDetails.class))).thenReturn(true);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/auth/validate")
                                .headers(httpHeaders)
                )
                .andExpect(status().is(200))
                .andReturn();

        verify(jwtService,times(1)).extractUsername(anyString());
//        verify(jwtService,times(1)).isTokenValid(anyString(), any(UserDetails.class));
    }

}





