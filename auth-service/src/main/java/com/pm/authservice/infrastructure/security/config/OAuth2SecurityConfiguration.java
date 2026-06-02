package com.pm.authservice.infrastructure.security.config;

import com.pm.authservice.infrastructure.security.filter.JwtAuthenticationFilter;
import com.pm.authservice.infrastructure.security.handler.CustomAccessDeniedHandler;
import com.pm.authservice.infrastructure.util.SecurityConstants;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@ConditionalOnExpression("${hazelcast.session.management.enabled}==false and '${auth.mode}'=='oauth2'")
@Configuration
@EnableWebSecurity
public class OAuth2SecurityConfiguration extends BaseSecurityConfig {

    @Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(SecurityConstants.AUTH_WHITELIST).permitAll()
                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex ->
                ex.authenticationEntryPoint(restAuthenticationEntryPoint())
                    .accessDeniedHandler(new CustomAccessDeniedHandler())
            );
        if (corsConfigEnabled) {
            httpSecurity.cors(c -> c.configurationSource(corsConfigurationSource()));
        }
        return httpSecurity.build();
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> disableJwtFilter(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        // JwtGrantedAuthoritiesConverter does not support nested claims — extract manually
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null) return List.of();
            @SuppressWarnings("unchecked")
            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
            if (roles == null) return List.of();
            return roles.stream()
                    .<GrantedAuthority>map(SimpleGrantedAuthority::new)
                    .toList();
        });
        return converter;
    }
}
