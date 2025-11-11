package com.pm.authservice.config.authentication;

import com.pm.authservice.util.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.SecurityFilterChain;

@ConditionalOnExpression("${hazelcast.session.management.enabled}==true")
@Configuration
@EnableWebSecurity
public class WebSecurityClusterConfiguration extends BaseSecurityConfig {
    protected static final Logger LOG = LoggerFactory.getLogger(WebSecurityClusterConfiguration.class);



    @Bean
    @Order(3)
    SecurityFilterChain clusterFilterChain(HttpSecurity http) throws Exception{
        LOG.info(" WebSecurityClusterConfiguration.WebSecurityClusterConfiguration ");
        http.csrf(AbstractHttpConfigurer::disable).sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(SecurityConstants.AUTH_WHITELIST).permitAll()).authorizeHttpRequests(requests -> requests
                        .anyRequest().hasAnyAuthority(getRoleNames()))
                .cors(Customizer.withDefaults()).exceptionHandling(handling -> handling.authenticationEntryPoint(restAuthenticationEntryPoint()))
                .logout(logout -> logout.logoutUrl("/logout").invalidateHttpSession(true).clearAuthentication(true).deleteCookies("JSESSIONID",
                        "USER_LOGGED_IN").logoutSuccessHandler((httpServletRequest, httpServletResponse, authentication) ->{
                    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                }));
        return http.build();
    }
}
