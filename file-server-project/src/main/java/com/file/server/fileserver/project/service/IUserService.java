package com.file.server.fileserver.project.service;

import com.file.server.fileserver.project.data.dto.AuthenticationRequest;
import com.file.server.fileserver.project.data.model.Users;

import java.util.List;

public interface IUserService {

    boolean verifyUser(String email);
    Users registerUser (AuthenticationRequest request);
    List<Users> getAllUsers();
    String validateToken(String token);
}
