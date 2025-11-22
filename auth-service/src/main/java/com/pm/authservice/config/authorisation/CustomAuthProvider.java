package com.pm.authservice.config.authorisation;


import com.pm.authservice.auth.UserDetailsDTO;
import com.pm.authservice.user.model.AccountStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CustomAuthProvider implements AuthenticationProvider {
    private static final Logger log = LoggerFactory.getLogger(CustomAuthProvider.class);

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomAuthProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        UserDetailsDTO user = null;

        try{
            user = (UserDetailsDTO) userDetailsService.loadUserByUsername(username);
        }
        catch (UsernameNotFoundException exc){
            throw new BadCredentialsException("error.user.not.found");
        }

        log.info("CustomAuthProvider->authenticate->user: {}" , user.getUsername());
        if (Objects.isNull(user)) {
            throw new BadCredentialsException("error.user.not.found");
        }


        if(isAccountNonActive(user.getStatus())){
            throw new BadCredentialsException("error.user.not.active");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("error.invalid.password");
        }

        return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private Boolean isAccountNonActive(AccountStatus status){
        if(AccountStatus.INACTIVE.equals(status) || AccountStatus.DELETED.equals(status)){
            return true;
        }
        return false;
    }
}
