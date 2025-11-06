package com.pm.authservice.config.authentication;

import com.pm.authservice.config.authorisation.CustomAuthProvider;
import com.pm.authservice.config.authorisation.NoAuthenticationRequestMatcher;
import com.pm.authservice.model.RoleEntity;
import com.pm.authservice.repository.RoleRepository;
import com.pm.authservice.util.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

public class BaseSecurityConfig {

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected NoAuthenticationRequestMatcher noAuthenticationRequestMatcher;

    @Autowired
    protected CustomAuthProvider customAuthProvider;

    protected String[] getRoleNames()  {
        List<String> roleNames = roleRepository.findAll().stream()
                .map(RoleEntity::getName)
                .toList();
        int len = roleNames.size();
        String[] roleNamesArr = new String[len];
        for (int i = 0; i < len; i++) {
            roleNamesArr[i] = roleNames.get(i);
        }
        return roleNamesArr;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring().requestMatchers(noAuthenticationRequestMatcher);
    }

    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(SecurityConstants.ALLOWED_ORIGIN_PATTERNS));
        configuration.setAllowedMethods(Arrays.asList(SecurityConstants.ALLOWED_METHODS));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList(SecurityConstants.ALLOWED_HEADERS ));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
