package util;

import com.pm.authservice.dto.RoleDTO;
import com.pm.authservice.dto.UserDTO;
import com.pm.authservice.dto.UserDetailsDTO;
import com.pm.authservice.model.AccountStatus;
import com.pm.authservice.model.RoleEntity;
import com.pm.authservice.model.UserEntity;
import com.pm.authservice.util.AuthorityConstants;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TestUtil {

    public static RoleEntity createTestRole(){
        RoleEntity role = new RoleEntity();
        role.setId(1);
        role.setName(AuthorityConstants.ROLE_SYSTEM_ADMIN);
        return role;
    }

    public static UserEntity createTestUser(final Integer userId){
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setFirstName("Robert");
        user.setLastName("Smith");
        user.setUsername("admin1");
        user.setPassword("123");
        user.setStatus(AccountStatus.ACTIVE);
        user.setEmail(TestConstants.TEST_USER_EMAIL);
        user.setRoles(Set.of(createTestRole()));
        user.setPublicId(UUID.fromString(TestConstants.TEST_USER_PUBLIC_ID));
        return user;
    }

    public static RoleDTO createTestRoleDTO(){
        RoleDTO role = new RoleDTO();
        role.setId(1);
        role.setName(AuthorityConstants.ROLE_SYSTEM_ADMIN);
        return role;
    }

    public static UserDTO createTestUserDto(final Integer userId){
        UserDTO userDto = new UserDTO();
        userDto.setFirstName("Robert");
        userDto.setLastName("Smith");
        userDto.setUsername("admin1");
        userDto.setPassword("123");
        userDto.setStatus(AccountStatus.fromValue(AccountStatus.ACTIVE));
        userDto.setEmail(TestConstants.TEST_USER_EMAIL);
        userDto.setPublicId(TestConstants.TEST_USER_PUBLIC_ID);
        userDto.setRoles(List.of(createTestRoleDTO()));
        return userDto;
    }

    public static UserDetailsDTO  createTestUserDetailsDTO(final Integer userId){
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setId(userId);
        userDetailsDTO.setFirstName("Robert");
        userDetailsDTO.setLastName("Smith");
        userDetailsDTO.setUsername("admin1");
        userDetailsDTO.setPassword("123");
        userDetailsDTO.setStatus(AccountStatus.ACTIVE);
        userDetailsDTO.setEmail(TestConstants.TEST_USER_EMAIL);
        userDetailsDTO.setPublicId(TestConstants.TEST_USER_PUBLIC_ID);
        return userDetailsDTO;
    }


}
