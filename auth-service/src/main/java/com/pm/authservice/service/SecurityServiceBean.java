package com.pm.authservice.service;

import com.pm.authservice.dto.UserDetailsDTO;
import com.pm.authservice.model.UserEntity;
import com.pm.authservice.util.AuthorityConstants;
import com.pm.authservice.util.UserUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SecurityServiceBean implements SecurityService {

    private final AuthService authService;
    private final  UserService usersService;

    public SecurityServiceBean(
            AuthService authService,
            UserService usersService
    ) {
        this.authService = authService;
        this.usersService = usersService;
    }


    @Override
    public boolean isSystemAdmin(UserEntity user) {
        return UserUtil.hasRole(user, AuthorityConstants.ROLE_SYSTEM_ADMIN);
    }
}
