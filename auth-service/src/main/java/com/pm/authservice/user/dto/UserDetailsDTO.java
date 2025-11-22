package com.pm.authservice.user.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.pm.authservice.config.authorisation.Email;
import com.pm.authservice.config.authorisation.UserEmail;
import com.pm.authservice.dto.serializer.CustomRoleDeserializer;
import com.pm.authservice.user.model.AccountStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class UserDetailsDTO implements UserEmail, UserDetails, Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String publicId;
    private String username;
    private String password;
    private String lastName;
    private String firstName;
    private Email email;
    private AccountStatus status;
    private Boolean isEnabled;
    private List<RoleDTO> roleEntities= new ArrayList<>();

    public UserDetailsDTO(String publicId,String username,String password,String email) {
        this.publicId = publicId;
        this.username = username;
        this.password = password;
        this.email = new Email(email);
    }


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

    @Override
    public Email getUserEmail() {
        return this.email;
    }
}
