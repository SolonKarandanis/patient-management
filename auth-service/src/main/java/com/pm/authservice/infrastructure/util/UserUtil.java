package com.pm.authservice.infrastructure.util;

import com.pm.authservice.infrastructure.persistence.entity.RoleJpaEntity;
import com.pm.authservice.infrastructure.persistence.entity.RoleOperationJpaEntity;
import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.util.StringUtils;

import java.util.*;

public class UserUtil {
    // cannot create a new instance
    private UserUtil() {
    }

    /**
     * Check if any of the role names for this user matches the given role name
     *
     * @param user
     * @param role
     * @return true / false
     */
    public static boolean hasRole(UserJpaEntity user, String role){
        boolean result = false;
        if(Objects.nonNull(user)){
            Set<RoleJpaEntity> userRoles = user.getRoles();
            RoleJpaEntity found=userRoles.stream()
                    .filter(ur -> role.equals(ur.getName()))
                    .findFirst()
                    .orElse(null);
            if(Objects.nonNull(found)){
                result = true;
            }
        }
        return result;
    }

    /**
     * Returns the list of operations for the specified user.
     *
     * @param currentUser
     * @return
     */
    public static List<String> getUserOperations(UserJpaEntity currentUser){
        List<String> userOperations = new ArrayList<>();
        for (RoleJpaEntity role : currentUser.getRoles()) {
            for (RoleOperationJpaEntity roleOperation : role.getRoleOperations()) {
                userOperations.add(roleOperation.getOperation().getName());
            }
        }
        return userOperations;
    }

    /**
     * Checks if the user has the specified operation.
     *
     * @param user
     * @param operation
     * @return
     */
    public static boolean hasOperation(UserJpaEntity user, String operation) {
        List<String> userOperations = getUserOperations(user);
        return userOperations.contains(operation);
    }

    /**
     * if user null, FALSE
     * if commaRoles empty/null TRUE
     * else check comma delimited ids to match user role ids.
     *
     * @param user
     * @param commaRoles
     * @return
     */
    public static boolean hasAnyRole(UserJpaEntity user, String commaRoles){
        boolean result = false;
        if(Objects.nonNull(user) && StringUtils.hasLength(commaRoles)){
            result = true; // no roles means success
        }
        else if(Objects.nonNull(user)){
            String[] roles = commaRoles.split(",");
            Set<RoleJpaEntity> userRoles = user.getRoles();
            for (RoleJpaEntity r : userRoles) {
                for (String role : roles) {
                    if (role.equalsIgnoreCase(r.getId().toString())) {
                        result = true;
                        break;
                    }
                }
                if (result) {
                    break;
                }
            }
        }
        return result;
    }

    public static boolean hasAllRoles(UserJpaEntity user, String commaRoles){
        Collection<String> testRoles = Arrays.asList(commaRoles.split(","));
        Collection<String> userRoles = new HashSet<String>();
        for (RoleJpaEntity role : user.getRoles()) {
            userRoles.add(role.getName());
        }
        return userRoles.containsAll(testRoles);
    }
}
