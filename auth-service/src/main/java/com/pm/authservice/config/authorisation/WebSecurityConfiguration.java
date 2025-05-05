package com.pm.authservice.config.authorisation;

import com.pm.authservice.model.Role;
import com.pm.authservice.repository.RoleRepository;
import com.pm.authservice.util.SecurityConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;


@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {
	
	@Value("${security.jwt.key}")
    private String signKey;
	private final JwtAuthenticationFilter jwtAuthFilter;
    private final NoAuthenticationRequestMatcher noAuthenticationRequestMatcher;
    private final CustomAuthProvider customAuthProvider;
    private final RoleRepository roleRepository;
	
	public WebSecurityConfiguration(
            JwtAuthenticationFilter jwtAuthFilter,
            NoAuthenticationRequestMatcher noAuthenticationRequestMatcher,
            CustomAuthProvider customAuthProvider,
            RoleRepository roleRepository) {
        this.jwtAuthFilter=jwtAuthFilter;
        this.noAuthenticationRequestMatcher=noAuthenticationRequestMatcher;
        this.customAuthProvider = customAuthProvider;
        this.roleRepository = roleRepository;
    }
	
	@Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity, RequestMatcherBuilder mvc) throws Exception {
		httpSecurity
		.csrf(AbstractHttpConfigurer::disable)
//                .cors(Customizer.withDefaults())
        .cors(c->c.configurationSource(corsConfigurationSource()))

        .authorizeHttpRequests(auth-> auth
                .requestMatchers(mvc.matchers(SecurityConstants.AUTH_WHITELIST)).permitAll()
                .requestMatchers(mvc.pattern(HttpMethod.POST, "/auth/login")).permitAll()
                .anyRequest()
                .hasAnyAuthority(getRoleNames())
        )
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(ex -> 
        	ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        		.accessDeniedHandler(new CustomAccessDeniedHandler())
        )
                .authenticationProvider(customAuthProvider);
		return httpSecurity.build();
	}

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

	
	@Bean
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
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring().requestMatchers(noAuthenticationRequestMatcher);
    }

    private String[] getRoleNames()  {
        List<String> roleNames = roleRepository.findAll().stream()
                .map(Role::getName)
                .toList();
        int len = roleNames.size();
        String[] roleNamesArr = new String[len];
        for (int i = 0; i < len; i++) {
            roleNamesArr[i] = roleNames.get(i);
        }
        return roleNamesArr;
    }
}
