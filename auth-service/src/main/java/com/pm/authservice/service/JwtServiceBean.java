package com.pm.authservice.service;

import com.pm.authservice.dto.JwtDTO;
import com.pm.authservice.dto.UserDetailsDTO;
import com.pm.authservice.exception.AuthException;
import com.pm.authservice.util.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceBean implements JwtService{
    @Value("${security.jwt.key}")
    private String signKey;

    private static final Logger log = LoggerFactory.getLogger(JwtServiceBean.class);

    @Override
    public String extractUsername(String token) throws AuthException{
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
            throws AuthException {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public Claims extractAllClaims(String token)
            throws AuthException{
        try{
            return Jwts
                    .parser()
                    .setSigningKey(signKey.getBytes())
                    .build()
                    .parseClaimsJws(token.replace(SecurityConstants.BEARER_TOKEN_PREFIX, ""))
                    .getBody();

        }catch (MalformedJwtException e) {
            log.error("Invalid  token: {}", e.getMessage());
            throw new AuthException("error.invalid.token");
        }
    }

    @Override
    public JwtDTO generateToken(UserDetailsDTO user) {
        return generateToken(new HashMap<>(), user);
    }

    @Override
    public JwtDTO generateToken(Map<String, Object> extraClaims, UserDetailsDTO user) {
        Date expireDate = new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME);
        String token = Jwts
                .builder()
                .claim("username", user.getUsername())
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName())
                .claim("email", user.getEmail())
                .claim("publicId", user.getPublicId())
//                .claim("authorities", user.getAuthorityEntities().stream().map(Authority::getName).toList())
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512,signKey.getBytes())
                .compact();
        return new JwtDTO(token, expireDate);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails)  throws AuthException{
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) throws AuthException{
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) throws AuthException{
        return extractClaim(token, Claims::getExpiration);
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(signKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
