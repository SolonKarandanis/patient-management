package com.pm.authservice.config.authorisation;

import com.pm.authservice.dto.UserDetailsDTO;
import com.pm.authservice.model.UserEntity;
import com.pm.authservice.service.SecurityService;
import com.pm.authservice.service.UserService;
import com.pm.authservice.util.UserUtil;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.Objects;
import java.util.UUID;


public class CustomMethodSecurityExpressionRoot
        extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    /** Logger for the class. */
    private static final Logger LOG = LoggerFactory.getLogger(CustomMethodSecurityExpressionRoot.class);

    private Object filterObject;

    private Object returnObject;

    @Setter
    private Object target;

    protected UserEntity currentUser;

    @Autowired
    protected SecurityService securityService;

    @Autowired
    protected UserService usersService;


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

    /* Custom permission functions */
    public boolean hasAnyPermission(final String[] targetDomainObject){
        boolean check = false;
        String username = this.currentUser.getUsername();
        LOG.info("AUTHORIZE: hasAnyPermission({}, {})",username, targetDomainObject);
        for (String operationName : targetDomainObject) {
            check = UserUtil.hasOperation(this.currentUser, operationName);
            if (check) {
                break;
            }
        }
        return check;
    }

    public boolean hasAllPermissions(final String[] targetDomainObject) {
        boolean check = false;
        String username = this.currentUser.getUsername();
        LOG.info("AUTHORIZE: hasAllPermissions({}, {})",username, targetDomainObject);
        for (String operationName : targetDomainObject) {
            check = UserUtil.hasOperation(this.currentUser, operationName);
            if (!check) {
                break;
            }
        }
        return check;
    }

    public boolean hasPermission(final String operationName){
        boolean check = false;
        String username = this.currentUser.getUsername();
        LOG.info("AUTHORIZE: hasPermission({}, {})",username, operationName);
        check = UserUtil.hasOperation(this.currentUser, operationName);
        return check;
    }


    public boolean isSystemAdmin() {
        return securityService.isSystemAdmin(this.currentUser);
    }

    public boolean isUserMe(String userId){
        return this.currentUser.getPublicId().equals(UUID.fromString(userId));
    }
}
