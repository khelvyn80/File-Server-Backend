package com.file.server.fileserver.project.data.authentication;

import com.file.server.fileserver.project.data.model.Role;
import com.file.server.fileserver.project.data.model.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UserAuthenticationDetails implements UserDetails {

    private String username;
    private String password;
    private boolean isEnabled;
    private Collection <? extends GrantedAuthority> authorities;

    public UserAuthenticationDetails (Users user){
        this.username = user.getEmail();
        this.password = user.getPassword();
        this.isEnabled = user.isEnabled();
        this.authorities = mapRolesAuthorities(user.getRole());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    private Collection<? extends GrantedAuthority> mapRolesAuthorities(Role role){
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
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
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }
}
