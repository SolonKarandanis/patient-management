package com.pm.authservice.infrastructure.application;

import com.pm.authservice.domain.port.out.UserPort;
import com.pm.authservice.infrastructure.web.exception.NotFoundException;
import com.pm.authservice.infrastructure.persistence.mapper.UserMapper;
import com.pm.authservice.infrastructure.web.dto.UserDTO;
import com.pm.authservice.user.service.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserQueryApplicationService implements UserQueryService {

    private static final String USER_NOT_FOUND = "error.user.not.found";

    private final UserPort userPort;
    private final UserMapper userMapper;

    public UserQueryApplicationService(UserPort userPort, UserMapper userMapper) {
        this.userPort = userPort;
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO viewUser(String publicId) {
        return userPort.findByDomainId(UUID.fromString(publicId))
                .map(userMapper::toDTO)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    @Override
    public List<String> getUserPermissions(String publicId) {
        return userPort.findPermissions(UUID.fromString(publicId));
    }
}
