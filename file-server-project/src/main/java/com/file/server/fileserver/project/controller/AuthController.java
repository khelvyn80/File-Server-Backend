package com.file.server.fileserver.project.controller;

import com.file.server.fileserver.project.data.dto.AuthenticationRequest;
import com.file.server.fileserver.project.data.events.hello.RegistrationCompleteEvent;
import com.file.server.fileserver.project.data.model.Users;
import com.file.server.fileserver.project.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@Component
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final ApplicationEventPublisher publisher;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
            @RequestBody AuthenticationRequest authenticationRequest,
            final HttpServletRequest request){
        try {
            var user = new Users();
            user.setEmail(authenticationRequest.email());
            user.setPassword(authenticationRequest.password());
            user.setRole(authenticationRequest.role());

            this.publisher.publishEvent(new RegistrationCompleteEvent(user, this.applicationUrl(request)));
            return ResponseEntity.ok("User created successfully");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String applicationUrl(HttpServletRequest request){
        String url = request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
        return url;
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyUser (@RequestParam("token") String token){
        try{
            if("valid".equalsIgnoreCase(this.userService.verifyUser(token))){
                return ResponseEntity.ok("User verified successfully");
            }
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return null;
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetpassword(String email, final HttpServletRequest request){
        try {
            String status = this.userService.resetPassword(email, applicationUrl(request));
            return ResponseEntity.ok(status);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resetpassword")
    public ResponseEntity<Void> validateResetToken(
            @RequestParam("token")String token,
            @RequestParam("passowrd")String password,
            @RequestParam("confirm") String confirm) throws URISyntaxException {
        try {
            String status = this.userService.validateResetToken(token);
            HttpHeaders headers = new HttpHeaders();
            if("valid".equalsIgnoreCase(status)){
                URI uri = new URI("/update?token="+token+"&password="+password);
                headers.setLocation(uri);
                return new ResponseEntity<>(headers, HttpStatus.FOUND);
            }
        }
        catch (Exception e){

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    @PutMapping("/update")
    public ResponseEntity<String> updatePassword(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirm") String confirm
    ){
        try{
            String email = this.userService.getUserByToken(token).getEmail();
            String status = this.userService.updatePassword(email, password, confirm);
            if ("updated".equalsIgnoreCase(status)){
                return ResponseEntity.ok("Password updated successfully");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return null;
    }


}
