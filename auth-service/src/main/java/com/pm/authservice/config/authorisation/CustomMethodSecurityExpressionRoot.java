package com.pm.authservice.config.authorisation;

import com.pm.authservice.dto.UserDTO;
import com.pm.authservice.dto.UserDetailsDTO;
import com.pm.authservice.model.UserEntity;
import com.pm.authservice.service.RoleService;
import com.pm.authservice.service.UserService;
import com.pm.authservice.util.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.Objects;


public class CustomMethodSecurityExpressionRoot
        extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    /** Logger for the class. */
    private static final Logger LOG = LoggerFactory.getLogger(CustomMethodSecurityExpressionRoot.class);

    private Object filterObject;

    private Object returnObject;

    private Object target;

    protected UserEntity currentUser;

    @Autowired
    protected UserService usersService;

    @Autowired
    protected RoleService roleService;

    public CustomMethodSecurityExpressionRoot(
            Authentication authentication,
            WebApplicationContext webAppContext
    ){
        super(authentication);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, Objects.requireNonNull(webAppContext.getServletContext()));

        UserDetailsDTO userDTO = (UserDetailsDTO) getAuthentication().getPrincipal();
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

    public void setTarget(Object target) {
        this.target = target;
    }

    /* Custom permission functions */
    public boolean hasAnyPermission(final String[] targetDomainObject){
        boolean check = false;
        String username = this.currentUser.getUsername();
        LOG.debug("AUTHORIZE: hasAnyPermission({}, {})",username, targetDomainObject);
        for (String operationName : targetDomainObject) {
            check = UserUtil.hasOperation(currentUser, operationName);
            if (check) {
                break;
            }
        }
        return check;
    }

    public boolean hasAllPermissions(final String[] targetDomainObject) {
        boolean check = false;
        String username = this.currentUser.getUsername();
        LOG.debug("AUTHORIZE: hasAllPermissions({}, {})",username, targetDomainObject);
        for (String operationName : targetDomainObject) {
            check = UserUtil.hasOperation(currentUser, operationName);
            if (!check) {
                break;
            }
        }
        return check;
    }

    public boolean hasPermission(final String operationName){
        boolean check = false;
        String username = this.currentUser.getUsername();
        LOG.debug("AUTHORIZE: hasPermission({}, {})",username, operationName);
        check = UserUtil.hasOperation(currentUser, operationName);
        return check;
    }
}
