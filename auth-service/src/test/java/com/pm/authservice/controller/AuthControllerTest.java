package com.pm.authservice.controller;

import com.pm.authservice.dto.JwtDTO;
import com.pm.authservice.dto.LoginRequestDTO;
import com.pm.authservice.dto.UserDetailsDTO;
import com.pm.authservice.exception.AuthException;
import com.pm.authservice.service.AuthService;
import com.pm.authservice.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import util.TestConstants;
import util.TestUtil;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("AuthControllerTest")
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {


    @InjectMocks
    protected AuthController controller;

    @Mock
    protected AuthService authService;

    @Mock
    protected JwtService jwtService;

    @Mock
    protected Authentication authentication;

    protected UserDetailsDTO userDto;

    protected final Integer userId = 1;

    @BeforeEach
    public void setup(){
        userDto = TestUtil.createTestUserDetailsDTO(userId);
    }

    @DisplayName("Successful Login")
    @Test
    void testLogin01() throws AuthException {
        LoginRequestDTO credentials = new LoginRequestDTO();
        credentials.setEmail(TestConstants.TEST_USER_EMAIL);
        credentials.setPassword("123");
        JwtDTO jwt = new JwtDTO(TestConstants.TEST_TOKEN,new Date());

        when(authService.authenticate(credentials)).thenReturn(userDto);
        when(jwtService.generateToken(userDto)).thenReturn(jwt);

        ResponseEntity<JwtDTO> resp = controller.authenticate(credentials);
        assertNotNull(resp);
        assertNotNull(resp.getBody());
        assertEquals(resp.getBody(), jwt);
        assertTrue(resp.getStatusCode().isSameCodeAs(HttpStatus.OK));

        verify(authService,times(1)).authenticate(credentials);
        verify(jwtService,times(1)).generateToken(userDto);
    }
}
