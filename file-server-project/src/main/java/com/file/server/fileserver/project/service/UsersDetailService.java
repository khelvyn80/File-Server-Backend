package com.file.server.fileserver.project.service;

import com.file.server.fileserver.project.data.authentication.UserAuthenticationDetails;
import com.file.server.fileserver.project.data.model.Role;
import com.file.server.fileserver.project.data.model.Users;
import com.file.server.fileserver.project.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UsersDetailService implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = this.usersRepository.findUsersByEmail(email)
                .orElseThrow( () -> new UsernameNotFoundException("User not found with email :"+email));
        return new UserAuthenticationDetails(user);
    }

    private Collection<? extends GrantedAuthority> mapRolesAuthorities(Role role){
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }
}
