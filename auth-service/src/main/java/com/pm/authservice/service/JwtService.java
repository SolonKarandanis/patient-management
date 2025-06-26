package com.pm.authservice.service;

import com.pm.authservice.dto.JwtDTO;
import com.pm.authservice.dto.UserDetailsDTO;
import com.pm.authservice.exception.AuthException;
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
