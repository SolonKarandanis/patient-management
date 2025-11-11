package com.pm.authservice.config.authentication;

import com.pm.authservice.config.authorisation.CustomAuthProvider;
import com.pm.authservice.config.authorisation.NoAuthenticationRequestMatcher;
import com.pm.authservice.model.RoleEntity;
import com.pm.authservice.repository.RoleRepository;
import com.pm.authservice.util.SecurityConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class BaseSecurityConfig {

    protected static final Logger LOG = LoggerFactory.getLogger(BaseSecurityConfig.class);

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

    @Bean
    public AuthenticationEntryPoint restAuthenticationEntryPoint() {
        LOG.info("in WebSecurityConfig.restAuthenticationEntryPoint");
        return new AuthenticationEntryPoint() {

            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response,
                                 AuthenticationException authException) throws IOException, ServletException {
                LOG.debug("RestAuthenticationEntryPoint called for request uri: {}", request.getRequestURI());
                // This is invoked when an anonymous user tries to access a
                // secured REST resource
                // We should just send a 401 Unauthorized response because there
                // is no 'login page' to redirect to
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        };
    }
}
