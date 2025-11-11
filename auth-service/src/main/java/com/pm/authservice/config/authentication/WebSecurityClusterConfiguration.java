package com.pm.authservice.config.authentication;

import com.pm.authservice.util.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@ConditionalOnExpression("${hazelcast.session.management.enabled}==true")
@Configuration
@EnableWebSecurity
public class WebSecurityClusterConfiguration extends BaseSecurityConfig {
    protected static final Logger LOG = LoggerFactory.getLogger(WebSecurityClusterConfiguration.class);

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    SecurityFilterChain clusterFilterChain(HttpSecurity http, SecurityContextRepository securityContextRepository) throws Exception{
        LOG.info(" WebSecurityClusterConfiguration.WebSecurityClusterConfiguration ");
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .securityContext(context -> context.securityContextRepository(securityContextRepository))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(SecurityConstants.AUTH_WHITELIST).permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .anyRequest().hasAnyAuthority(getRoleNames())
                )
                .cors(c->
                        c.configurationSource(corsConfigurationSource())
                )
                .exceptionHandling(handling ->
                        handling.authenticationEntryPoint(restAuthenticationEntryPoint())
                )
                .logout(logout ->
                        logout.logoutUrl("/logout")
                                .invalidateHttpSession(true)
                                .clearAuthentication(true)
                                .deleteCookies("JSESSIONID")
                                .logoutSuccessHandler((httpServletRequest, httpServletResponse, authentication) ->{
                                    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                                })
                );
        return http.build();
    }
}
