package com.file.server.fileserver.project.service;

import com.file.server.fileserver.project.data.dto.AuthenticationRequest;
import com.file.server.fileserver.project.data.model.Users;
import com.file.server.fileserver.project.exceptions.UserAlreadyExistException;
import com.file.server.fileserver.project.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public boolean verifyUser(String email) {
        return false;
    }

    @Override
    public Users registerUser(AuthenticationRequest request) {
        Optional<Users> user = this.usersRepository.findUsersByEmail(request.email());
        if (user.isPresent()){
            throw new UserAlreadyExistException("User already exist");
        }
        var newUser = new Users();
        newUser.setEmail(request.email());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRole(request.role());

        return this.usersRepository.save(newUser);

    }

    @Override
    public List<Users> getAllUsers() {
        return this.usersRepository.findAll();
    }

    @Override
    public String validateToken(String token) {
        return "";
    }
}
