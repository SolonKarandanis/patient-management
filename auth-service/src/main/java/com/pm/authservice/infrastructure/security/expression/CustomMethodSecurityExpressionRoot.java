package com.pm.authservice.infrastructure.security.expression;

import com.pm.authservice.infrastructure.web.dto.UserDetailsDTO;
import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import com.pm.authservice.infrastructure.persistence.repository.UserJpaRepository;
import com.pm.authservice.domain.model.AuthorityConstants;
import com.pm.authservice.infrastructure.util.UserUtil;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
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

    protected UserJpaEntity currentUser;

    @Autowired
    protected UserJpaRepository userRepository;


    public CustomMethodSecurityExpressionRoot(
            Authentication authentication,
            WebApplicationContext webAppContext
    ){
        super(authentication);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, Objects.requireNonNull(webAppContext.getServletContext()));
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("email");
            this.currentUser = this.userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found: " + email));
        } else {
            UserDetailsDTO userDTO = (UserDetailsDTO) principal;
            this.currentUser = this.userRepository.findByDomainId(UUID.fromString(userDTO.getPublicId()))
                    .orElseThrow(() -> new RuntimeException("User not found: " + userDTO.getPublicId()));
        }
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
        check= userRepository.existsByDomainIdAndOperationName(this.currentUser.getDomainId(), operationName);
        return check;
    }


    public boolean isSystemAdmin() {
        return userRepository.existsByDomainIdAndRoleName(this.currentUser.getDomainId(), AuthorityConstants.ROLE_SYSTEM_ADMIN);
    }

    public boolean isUserMe(String userId){
        return this.currentUser.getDomainId().equals(UUID.fromString(userId));
    }
}
