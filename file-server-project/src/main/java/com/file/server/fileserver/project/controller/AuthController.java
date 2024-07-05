package com.file.server.fileserver.project.controller;

import com.file.server.fileserver.project.data.dto.AuthenticationRequest;
import com.file.server.fileserver.project.data.events.hello.RegistrationCompleteEvent;
import com.file.server.fileserver.project.data.model.Users;
import com.file.server.fileserver.project.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
