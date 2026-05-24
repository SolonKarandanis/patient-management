package com.pm.authservice.controller;

import com.pm.authservice.infrastructure.web.controller.AuthController;
import com.pm.authservice.infrastructure.web.dto.JwtDTO;
import com.pm.authservice.infrastructure.web.dto.LoginRequestDTO;
import com.pm.authservice.domain.model.AccountStatus;
import com.pm.authservice.domain.model.User;
import com.pm.authservice.domain.port.in.AuthenticateUserUseCase;
import com.pm.authservice.infrastructure.web.exception.AuthException;
import com.pm.authservice.infrastructure.security.JwtService;
import com.pm.authservice.infrastructure.web.dto.UserDetailsDTO;
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
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("AuthControllerTest")
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @InjectMocks
    protected AuthController controller;

    @Mock
    protected AuthenticateUserUseCase authenticateUserUseCase;

    @Mock
    protected JwtService jwtService;

    @Mock
    protected Authentication authentication;

    protected UserDetailsDTO userDetailsDTO;
    protected User domainUser;

    protected final Integer userId = 1;

    @BeforeEach
    public void setup() {
        userDetailsDTO = TestUtil.createTestUserDetailsDTO(userId);
        domainUser = User.builder()
                .domainId(UUID.fromString(TestConstants.TEST_USER_PUBLIC_ID))
                .username("admin1")
                .email(TestConstants.TEST_USER_EMAIL)
                .firstName("Robert")
                .lastName("Smith")
                .password("hashed")
                .status(AccountStatus.ACTIVE)
                .isEnabled(true)
                .roles(Set.of())
                .build();
    }

    @DisplayName("Successful Login")
    @Test
    void testLogin01() throws AuthException {
        LoginRequestDTO credentials = new LoginRequestDTO();
        credentials.setEmail(TestConstants.TEST_USER_EMAIL);
        credentials.setPassword("123");
        JwtDTO jwt = new JwtDTO(TestConstants.TEST_TOKEN, new Date());

        when(authenticateUserUseCase.authenticate(credentials.getEmail(), credentials.getPassword()))
                .thenReturn(domainUser);
        when(jwtService.generateToken(any(UserDetailsDTO.class))).thenReturn(jwt);

        ResponseEntity<JwtDTO> resp = controller.authenticate(credentials);
        assertNotNull(resp);
        assertNotNull(resp.getBody());
        assertEquals(resp.getBody(), jwt);
        assertTrue(resp.getStatusCode().isSameCodeAs(HttpStatus.OK));

        verify(authenticateUserUseCase, times(1)).authenticate(credentials.getEmail(), credentials.getPassword());
        verify(jwtService, times(1)).generateToken(any(UserDetailsDTO.class));
    }
}
