package com.pm.authservice.config.authentication;

import com.pm.authservice.config.authorisation.CustomAccessDeniedHandler;
import com.pm.authservice.config.authorisation.JwtAuthenticationFilter;
import com.pm.authservice.util.SecurityConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@ConditionalOnExpression("${hazelcast.session.management.enabled}==false")
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends BaseSecurityConfig{
	
	@Value("${security.jwt.key}")
    private String signKey;
	private final JwtAuthenticationFilter jwtAuthFilter;
	
	public WebSecurityConfiguration(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter=jwtAuthFilter;
    }
	
	@Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
		.csrf(AbstractHttpConfigurer::disable)
        .cors(c->c.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(auth-> auth
                .requestMatchers(SecurityConstants.AUTH_WHITELIST).permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                .anyRequest()
                .hasAnyAuthority(getRoleNames())
        )
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(ex -> 
        	ex.authenticationEntryPoint(restAuthenticationEntryPoint())
        		.accessDeniedHandler(new CustomAccessDeniedHandler())
        )
                .authenticationProvider(customAuthProvider);
		return httpSecurity.build();
	}
}
