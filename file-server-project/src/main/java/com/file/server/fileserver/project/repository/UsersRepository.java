package com.file.server.fileserver.project.repository;

import com.file.server.fileserver.project.data.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findUsersByEmail(String email);
    Optional<Users> findUsersByVerificationToken (String token);

}
