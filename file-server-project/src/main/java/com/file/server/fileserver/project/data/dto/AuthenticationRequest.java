package com.file.server.fileserver.project.data.dto;

import com.file.server.fileserver.project.data.model.Role;

public record AuthenticationRequest(
        String email,
        String password,
        Role role
) {
}
