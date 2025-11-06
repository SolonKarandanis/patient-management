package com.pm.authservice.config.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@ConditionalOnExpression("${hazelcast.session.management.enabled}==true")
@Configuration
@EnableWebSecurity
public class WebSecurityClusterConfiguration extends BaseSecurityConfig {
    protected static final Logger LOG = LoggerFactory.getLogger(WebSecurityClusterConfiguration.class);

    @Bean
    @Order(1)
    SecurityFilterChain basicFilterChain(HttpSecurity http) throws Exception {
        LOG.info(" WebSecurityClusterConfiguration.basicFilterChain ");

        http.securityMatcher(EndpointRequest.toAnyEndpoint()).csrf(AbstractHttpConfigurer::disable)
                .cors(c->c.configurationSource(corsConfigurationSource()))
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(customizer -> customizer.requestMatchers(EndpointRequest.toAnyEndpoint()).authenticated());

        return http.build();
    }

//    @Bean
//    @Order(3)
//    SecurityFilterChain clusterFilterChain(HttpSecurity http) throws Exception{
//        LOG.info(" WebSecurityClusterConfiguration.WebSecurityClusterConfiguration ");
//        http.csrf(AbstractHttpConfigurer::disable).sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
//                .addFilterBefore(singleSignOutFilter, UsernamePasswordAuthenticationFilter.class)
//                .authorizeHttpRequests(requests -> requests
//                        .requestMatchers(getPermittedUrisAntPathRequestMatchers()).permitAll()).authorizeHttpRequests(requests -> requests
//                        .requestMatchers(getAuthenticationPermittedUrisAntPathRequestMatchers()).authenticated()
//                        .anyRequest().hasAnyAuthority(getRoleNames()))
//                .cors(withDefaults()).exceptionHandling(handling -> handling.authenticationEntryPoint(casRestAuthenticationEntryPoint()))
//                .logout(logout -> logout.logoutUrl("/logout").invalidateHttpSession(true).clearAuthentication(true).deleteCookies("JSESSIONID",
//                        "USER_LOGGED_IN").logoutSuccessHandler((httpServletRequest, httpServletResponse, authentication) ->{
//                    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
//                }));
//        return http.build();
//    }
}
