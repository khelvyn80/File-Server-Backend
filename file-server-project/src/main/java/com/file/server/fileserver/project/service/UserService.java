package com.file.server.fileserver.project.service;

import com.file.server.fileserver.project.data.dto.AuthenticationRequest;
import com.file.server.fileserver.project.data.model.Users;
import com.file.server.fileserver.project.exceptions.BadRequestException;
import com.file.server.fileserver.project.exceptions.NotFoundException;
import com.file.server.fileserver.project.exceptions.UserAlreadyExistException;
import com.file.server.fileserver.project.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public String verifyUser(String token) {
            var user = this.usersRepository.findUsersByVerificationToken(token);
            if(user.isEmpty()){
                throw new UsernameNotFoundException("User does not exist");
            }
            if(user.isPresent()){
                if(user.get().isEnabled()){
                    throw new BadRequestException("User is already registered");
                } else if (user.get().getTokenExpiry().getTime()-Calendar.getInstance().getTimeInMillis()<=0) {
                    throw new BadRequestException("Token is expired");
                }
                else {
                    user.get().setEnabled(true);
                    usersRepository.save(user.get());
                    return "valid";
                }
            }
            return "invalid";
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
        Optional <Users> user = this.usersRepository.findUsersByVerificationToken(token);
        if (user.isEmpty()){
            throw new NotFoundException("Invalid Verification Token");
        }
        if (this.tokenExpired(user.get().getTokenExpiry())){
            throw new BadRequestException("Verification Token Expired");
        }
        user.get().setEnabled(true);
        user.get().setVerificationToken(null);
        user.get().setTokenExpiry(null);
        this.usersRepository.save(user.get());


        return "verified";
    }

    private boolean tokenExpired(Date tokenDate){
        Calendar calendar = Calendar.getInstance();
        return tokenDate.getTime() - calendar.getTime().getTime() <= 0;
    }

}
