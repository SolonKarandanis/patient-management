package com.pm.authservice.service;

import com.pm.authservice.dto.LoginRequestDTO;
import com.pm.authservice.user.dto.UserDetailsDTO;
import com.pm.authservice.exception.AuthException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public interface AuthService {
    Authentication getAuthContext();
    UserDetailsDTO getLoggedUser();
    void setAuthentication(UsernamePasswordAuthenticationToken authenticationToken);
    void setAuthentication(Authentication authentication);
    UserDetailsDTO authenticate(LoginRequestDTO loginRequest) throws AuthException;
}
