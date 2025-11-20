package com.pm.authservice.util;

import com.pm.authservice.model.RoleEntity;
import com.pm.authservice.model.RoleOperationEntity;
import com.pm.authservice.user.model.UserEntity;
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
    public static boolean hasRole(UserEntity user, String role){
        boolean result = false;
        if(Objects.nonNull(user)){
            Set<RoleEntity> userRoles = user.getRoles();
            RoleEntity found=userRoles.stream()
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
    public static List<String> getUserOperations(UserEntity currentUser){
        List<String> userOperations = new ArrayList<>();
        for (RoleEntity role : currentUser.getRoles()) {
            for (RoleOperationEntity roleOperation : role.getRoleOperations()) {
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
    public static boolean hasOperation(UserEntity user, String operation) {
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
    public static boolean hasAnyRole(UserEntity user, String commaRoles){
        boolean result = false;
        if(Objects.nonNull(user) && StringUtils.hasLength(commaRoles)){
            result = true; // no roles means success
        }
        else if(Objects.nonNull(user)){
            String[] roles = commaRoles.split(",");
            Set<RoleEntity> userRoles = user.getRoles();
            for (RoleEntity r : userRoles) {
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

    public static boolean hasAllRoles(UserEntity user, String commaRoles){
        Collection<String> testRoles = Arrays.asList(commaRoles.split(","));
        Collection<String> userRoles = new HashSet<String>();
        for (RoleEntity role : user.getRoles()) {
            userRoles.add(role.getName());
        }
        return userRoles.containsAll(testRoles);
    }
}
