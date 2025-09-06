package com.pm.authservice.service;

import com.pm.authservice.dto.UserDTO;
import com.pm.authservice.dto.UserDetailsDTO;
import com.pm.authservice.model.UserEntity;
import com.pm.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import util.TestUtil;

import java.util.List;

@DisplayName("UserServiceBeanTest")
@ExtendWith(MockitoExtension.class)
public class UserServiceBeanTest {

    @InjectMocks
    protected UserService userService;

    @Mock
    protected UserRepository userRepository;

    @Mock
    protected RoleService roleService;

    @Mock
    protected VerificationTokenService verificationTokenService;

    @Mock
    protected PasswordEncoder passwordEncoder;

    protected final Integer userId = 1;
    protected UserEntity user;
    protected UserDTO userDto;
    protected UserDetailsDTO detailsDTO;
    protected List<UserDTO> dtos;

    @BeforeEach
    public void setup(){
        user = TestUtil.createTestUser(userId);
        userDto = TestUtil.createTestUserDto(userId);
        detailsDTO = TestUtil.createTestUserDetailsDTO(userId);
        dtos = List.of(userDto);
    }
}
