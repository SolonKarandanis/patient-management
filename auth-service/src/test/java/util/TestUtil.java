package util;

import com.pm.authservice.auth.UserDetailsDTO;
import com.pm.authservice.dto.Paging;
import com.pm.authservice.user.dto.RoleDTO;
import com.pm.authservice.user.model.AccountStatus;
import com.pm.authservice.user.model.RoleEntity;
import com.pm.authservice.user.dto.*;
import com.pm.authservice.user.model.UserEntity;
import com.pm.authservice.util.AuthorityConstants;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
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
        userDto.setStatus(AccountStatus.fromValue(AccountStatus.ACTIVE));
        userDto.setEmail(TestConstants.TEST_USER_EMAIL);
        userDto.setPublicId(TestConstants.TEST_USER_PUBLIC_ID);
        userDto.setRoles(List.of(createTestRoleDTO()));
        return userDto;
    }

    public static UserDetailsDTO createTestUserDetailsDTO(final Integer userId){
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO(
                TestConstants.TEST_USER_PUBLIC_ID,"admin1","123",TestConstants.TEST_USER_EMAIL);
        userDetailsDTO.setId(userId);
        userDetailsDTO.setFirstName("Robert");
        userDetailsDTO.setLastName("Smith");
        userDetailsDTO.setStatus(AccountStatus.ACTIVE);
        return userDetailsDTO;
    }

    public static Authentication getTestAuthenticationFromUserDTO(UserDetailsDTO userDto, String token) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDto, token, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return SecurityContextHolder.getContext().getAuthentication();

    }


    public static Authentication getTestAuthenticationFromUserDTO(UserDetailsDTO userDto) {
        return getTestAuthenticationFromUserDTO(userDto, TestConstants.TEST_TOKEN);
    }

    public static Paging createPaging(int pagingSize, int pagingIndex, String sortCol, String sortDirection) {
        Paging output = new Paging();
        output.setPagingSize(pagingSize);
        output.setPagingStart(pagingIndex);
        output.setSortingColumn(sortCol);
        output.setSortingDirection(sortDirection);
        return output;
    }

    public static UsersSearchRequestDTO generateUsersSearchRequestDTO(){
        UsersSearchRequestDTO dto = new UsersSearchRequestDTO();
        dto.setPaging(createPaging(10,1,"id","ASC"));
        return dto;
    }

    public static CreateUserDTO createTestCreateUserDTO(){
        CreateUserDTO dto = new CreateUserDTO();
        dto.setFirstName("Robert");
        dto.setLastName("Smith");
        dto.setUsername("admin1");
        dto.setPassword("123");
        return dto;
    }

}
