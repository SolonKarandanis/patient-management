package com.pm.authservice.infrastructure.application;

import com.pm.authservice.domain.port.out.RolePort;
import com.pm.authservice.infrastructure.web.dto.RoleDTO;
import com.pm.authservice.infrastructure.application.RoleQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoleApplicationService implements RoleQueryService {

    private final RolePort rolePort;

    public RoleApplicationService(RolePort rolePort) {
        this.rolePort = rolePort;
    }

    @Override
    public List<RoleDTO> findAllRoles() {
        return rolePort.findAll().stream()
                .map(r -> new RoleDTO(r.getId(), r.getName()))
                .toList();
    }
}
