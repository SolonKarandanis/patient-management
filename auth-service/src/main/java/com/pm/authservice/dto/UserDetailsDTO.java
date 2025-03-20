package com.pm.authservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.pm.authservice.dto.serializer.CustomRoleDeserializer;
import com.pm.authservice.model.AccountStatus;
import com.pm.authservice.model.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class UserDetailsDTO implements UserDetails {

    private Integer id;
    private String publicId;
    private String username;
    private String password;
    private String lastName;
    private String firstName;
    private String email;
    private AccountStatus status;
    private Boolean isEnabled;
    private List<Role> roleEntities= new ArrayList<>();
    private List<String> roleNames = new ArrayList<>();


    @JsonDeserialize(using = CustomRoleDeserializer.class)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleEntities.stream()
                .map(role-> new SimpleGrantedAuthority(role.getName()))
                .toList();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }
}
