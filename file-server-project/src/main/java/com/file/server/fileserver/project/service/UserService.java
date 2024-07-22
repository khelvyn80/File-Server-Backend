package com.file.server.fileserver.project.service;

import com.file.server.fileserver.project.data.dto.AuthenticationRequest;
import com.file.server.fileserver.project.data.model.Users;
import com.file.server.fileserver.project.exceptions.BadRequestException;
import com.file.server.fileserver.project.exceptions.NotFoundException;
import com.file.server.fileserver.project.exceptions.UserAlreadyExistException;
import com.file.server.fileserver.project.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
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

                }
                Calendar calendar = Calendar.getInstance();
                System.out.println(user.get().getTokenExpiry().getTime()-calendar.getTime().getTime());
                if (user.isPresent() && user.get().getTokenExpiry().getTime() - calendar.getTime().getTime() >= 0){
                    throw new BadRequestException("Verification Token is Expired");
                }
                else {
                    user.get().setEnabled(true);
                    user.get().setTokenExpiry(null);
                    user.get().setVerificationToken(null);
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
        newUser.setRole(request.role());
        newUser.setPassword(this.passwordEncoder.encode(request.password()));
        return this.usersRepository.save(newUser);

    }

    @Override
    public List<Users> getAllUsers() {
        return this.usersRepository.findAll();
    }

    public Users getUserByToken(String token){
        var user = this.usersRepository.findUsersByVerificationToken(token);
        if(user.isEmpty()){
            throw new UsernameNotFoundException("User does not exist");
        }
        return user.get();
    }




    public String resetPassword(String email, String url){
        var user = this.usersRepository.findUsersByEmail(email);
        if(user.isEmpty()){
            throw new UsernameNotFoundException("User with email "+email+" not found");
        }
        String resetToken = UUID.randomUUID().toString();
        user.get().setVerificationToken(resetToken);
        user.get().setTokenExpiry(this.getTokenExpirationTime());
        this.usersRepository.save(user.get());
        String resetLink = url+"resetpassword?token="+resetToken;
        log.info("Click on the link to reset your password {}", resetLink);
        return resetLink;
    }


    public String validateResetToken(String token){
        System.out.println("I am starting the process");
        var user = this.usersRepository.findUsersByVerificationToken(token);


            return "valid";

    }

    public String updatePassword(String email, String password, String confirm){
        var user = this.usersRepository.findUsersByEmail(email);
        if (user.isEmpty()){
            throw new UsernameNotFoundException("User with email provided does not exist");
        }

        if (!user.get().isEnabled()){
            throw new BadRequestException("Please verify your account first");
        }
        if(!password.equals(confirm)){
            throw new BadRequestException("Passwords do not match");
        }
        user.get().setPassword(this.passwordEncoder.encode(password));
        this.usersRepository.save(user.get());
        log.info("Passowrd updated successfully");
        return "updated";
    }

    private static final int EXPIRATIONTIME = 15;

    /**
     * @return Date
     */
    // A method that handles the calculation of the expiration time.
    protected Date getTokenExpirationTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, EXPIRATIONTIME);
        return new Date(calendar.getTime().getTime());
    }
}
