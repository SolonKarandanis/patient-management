package com.pm.authservice.config.authorisation;

import com.pm.authservice.dto.UserDTO;
import com.pm.authservice.model.UserEntity;
import com.pm.authservice.service.RoleService;
import com.pm.authservice.service.SecurityService;
import com.pm.authservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class CustomMethodSecurityExpressionRoot
        extends SecurityExpressionRoot implements MethodSecurityExpressionOperations, SecurityService {

    /** Logger for the class. */
    private static final Logger LOG = LoggerFactory.getLogger(CustomMethodSecurityExpressionRoot.class);

    private Object filterObject;

    private Object returnObject;

    private Object target;

    protected UserEntity currentUser;

    protected UserService usersService;
    protected RoleService roleService;

    public CustomMethodSecurityExpressionRoot(
            Authentication authentication,
            UserService usersService,
            RoleService roleService
    ){
        super(authentication);
        this.usersService = usersService;
        this.roleService = roleService;

        UserDTO userDTO = (UserDTO) getAuthentication().getPrincipal();
        this.currentUser = this.usersService.findByPublicId(userDTO.getPublicId());
        this.setDefaultRolePrefix(""); // For using hasRole as: hasRole(EDConstants.ROLE_XX)
    }


    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public Object getThis() {
        return this.target;
    }
}
