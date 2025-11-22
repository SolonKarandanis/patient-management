package com.pm.authservice.config.authorisation;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.authservice.dto.RoleDTO;
import com.pm.authservice.user.dto.UserDetailsDTO;
import com.pm.authservice.exception.AuthException;
import com.pm.authservice.user.model.AccountStatus;
import com.pm.authservice.service.JwtService;
import com.pm.authservice.util.SecurityConstants;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{

	private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	@Autowired
	private JwtService jwtService;

	@Autowired
	private ObjectMapper mapper;


	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain
	) throws ServletException, IOException {
		if (isWhitelisted(request)) {
			filterChain.doFilter(request, response);
			return;
		}
		final String authHeader = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
	    final String jwt;
	    final String username;
	    if (!isAuthorizationHeader(authHeader)) {
	        filterChain.doFilter(request, response);
	        return;
	    }
	    jwt = authHeader.substring(SecurityConstants.BEARER_TOKEN_PREFIX.length());
        try {
            username = jwtService.extractUsername(jwt);
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UsernamePasswordAuthenticationToken authToken = getAuthentication(request,jwt);
				SecurityContextHolder.getContext().setAuthentication(authToken);
				filterChain.doFilter(request, response);
			}
        } catch (AuthException e) {
            throw new RuntimeException(e);
        }
	}

	private boolean isWhitelisted(HttpServletRequest request) {
		String requestURI = request.getRequestURI();
		return Arrays.stream(SecurityConstants.AUTH_WHITELIST)
				.anyMatch(pattern -> requestURI.startsWith(pattern.replace("/**", "")));
	}
	
	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request,String jwt) throws AuthException {
		 String token = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
		 if (token != null) {
             Claims claims = null;
             try {
                 claims = jwtService.extractAllClaims(token);
                 String publicId = claims.get("publicId", String.class);
                 String username = claims.get("sub", String.class);
                 String password = claims.get("password", String.class);
                 Email email = mapper.convertValue(claims.get("email"), Email.class);
                 UserDetailsDTO user = new UserDetailsDTO(publicId,username, password,email.toString());
				 user.setFirstName(claims.get("firstName", String.class));
				 user.setLastName(claims.get("lastName", String.class));
				 user.setStatus(AccountStatus.fromValue(claims.get("status", String.class)));
				 if (user.getUsername() != null && jwtService.isTokenValid(jwt, user)) {
					 List<RoleDTO> roleClaims = mapper.convertValue(claims.get("roles", List.class), new TypeReference<List<RoleDTO>>() { });
					 for(RoleDTO role : roleClaims){
						 log.info("JwtAuthenticationFilter -> claims -> roles : {}",role.getName());
					 }
					 List<SimpleGrantedAuthority> simpleGrantedAuthorities= roleClaims.stream()
							 .map(role->new SimpleGrantedAuthority(role.getName()))
							 .toList();
					 return new UsernamePasswordAuthenticationToken(user, null, simpleGrantedAuthorities);
				 }
             } catch (AuthException e) {
                 throw new RuntimeException(e);
             }
		 }
		 return null;
	 }
	
	private boolean isAuthorizationHeader(String authHeader) {
        if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith(SecurityConstants.BEARER_TOKEN_PREFIX) ) {
            return false;
        } 
        return true;
	 }
}
//List<String> authorities = mapper.convertValue(claims.get("authorities", List.class), new TypeReference<List<String>>() { })