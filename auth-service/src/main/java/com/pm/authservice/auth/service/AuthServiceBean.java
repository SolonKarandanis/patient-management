package com.pm.authservice.auth.service;

import com.pm.authservice.auth.dto.LoginRequestDTO;
import com.pm.authservice.auth.UserDetailsDTO;
import com.pm.authservice.exception.AuthException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceBean implements AuthService{
    private final AuthenticationManager authenticationManager;

    public AuthServiceBean(AuthenticationManager authenticationManager) {
        this.authenticationManager=authenticationManager;
    }

    @Override
    public Authentication getAuthContext() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public UserDetailsDTO getLoggedUser() {
        return (UserDetailsDTO) getAuthContext().getPrincipal();
    }

    @Override
    public void setAuthentication(UsernamePasswordAuthenticationToken authenticationToken) {
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Override
    public void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    public UserDetailsDTO authenticate(LoginRequestDTO loginRequest) throws AuthException {
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        try{
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            setAuthentication(authentication);
            return (UserDetailsDTO) authentication.getPrincipal();
        }
        catch (AuthenticationException exc){
            throw new AuthException(exc.getMessage());
        }
    }
}
