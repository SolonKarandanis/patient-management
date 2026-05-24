package com.pm.authservice.infrastructure.security;

import com.pm.authservice.infrastructure.web.dto.JwtDTO;
import com.pm.authservice.infrastructure.web.dto.UserDetailsDTO;
import com.pm.authservice.infrastructure.web.exception.AuthException;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

public interface JwtService {
    String extractUsername(String token) throws AuthException;
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws AuthException;
    Claims extractAllClaims(String token) throws AuthException;
    JwtDTO generateToken(UserDetailsDTO user);
    JwtDTO generateToken(Map<String, Object> extraClaims, UserDetailsDTO user);
    boolean isTokenValid(String token, UserDetails userDetails)throws AuthException;
}
