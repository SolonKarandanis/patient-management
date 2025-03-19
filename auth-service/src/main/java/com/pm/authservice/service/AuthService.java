package com.pm.authservice.service;

import com.pm.authservice.dto.LoginRequestDTO;
import com.pm.authservice.dto.UserDetailsDTO;
import com.pm.authservice.exception.AuthException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public interface AuthService {
    public Authentication getAuthContext();
    public UserDetailsDTO getLoggedUser();
    public void setAuthentication(UsernamePasswordAuthenticationToken authenticationToken);
    public void setAuthentication(Authentication authentication);
    public UserDetailsDTO authenticate(LoginRequestDTO loginRequest) throws AuthException;
}
