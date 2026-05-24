package com.pm.authservice.infrastructure.security;

import com.pm.authservice.infrastructure.web.dto.UserDetailsDTO;
import com.pm.authservice.domain.model.User;
import com.pm.authservice.domain.port.out.UserPort;
import com.pm.authservice.infrastructure.web.dto.RoleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserDetailsServiceBean implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceBean.class);
    private static final String USER_NOT_FOUND = "error.user.not.found";

    private final UserPort userPort;

    public UserDetailsServiceBean(UserPort userPort) {
        this.userPort = userPort;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userPort.findByEmail(email)
                .map(this::toUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
    }

    private UserDetails toUserDetails(User user) {
        UserDetailsDTO dto = new UserDetailsDTO(
                user.getDomainId().toString(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail()
        );
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setIsEnabled(user.getIsEnabled());
        dto.setStatus(user.getStatus());
        if (user.getRoles() != null) {
            dto.setRoleEntities(user.getRoles().stream()
                    .map(r -> new RoleDTO(r.getId(), r.getName()))
                    .toList());
        }
        return dto;
    }
}
