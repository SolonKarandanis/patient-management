package com.pm.authservice.service;

import com.pm.authservice.dto.JwtDTO;
import com.pm.authservice.dto.UserDetailsDTO;
import com.pm.authservice.exception.AuthException;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

public interface JwtService {
    public String extractUsername(String token) throws AuthException;
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws AuthException;
    public Claims extractAllClaims(String token) throws AuthException;
    public JwtDTO generateToken(UserDetailsDTO user);
    public JwtDTO generateToken(Map<String, Object> extraClaims, UserDetailsDTO user);
    public boolean isTokenValid(String token, UserDetails userDetails)throws AuthException;
}
