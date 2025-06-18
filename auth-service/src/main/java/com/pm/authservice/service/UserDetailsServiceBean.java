package com.pm.authservice.service;

import com.pm.authservice.dto.RoleDTO;
import com.pm.authservice.dto.UserDetailsDTO;
import com.pm.authservice.model.UserEntity;
import com.pm.authservice.repository.UserRepository;
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
    private static final String USER_NOT_FOUND="error.user.not.found";

    private final UserRepository userRepository;

    public UserDetailsServiceBean(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(this::createSpringSecurityUser)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
    }

    private UserDetails createSpringSecurityUser(UserEntity user){
        UserDetailsDTO dto = new UserDetailsDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setIsEnabled(user.getIsEnabled());
        dto.setPublicId(user.getPublicId().toString());
        dto.setStatus(user.getStatus());
        dto.setRoleEntities(user.getRoles()
                .stream()
                .map(role-> new RoleDTO(role.getId(), role.getName()))
                .toList());
        return dto;
    }
}
