package com.pm.authservice.auth.service;

import com.pm.authservice.auth.dto.LoginRequestDTO;
import com.pm.authservice.auth.dto.UserDetailsDTO;
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
