package com.file.server.fileserver.project.service;

import com.file.server.fileserver.project.data.dto.AuthenticationRequest;
import com.file.server.fileserver.project.data.model.Users;

import java.util.List;

public interface IUserService {

    String verifyUser(String token);
    Users registerUser (AuthenticationRequest request);
    List<Users> getAllUsers();

}
