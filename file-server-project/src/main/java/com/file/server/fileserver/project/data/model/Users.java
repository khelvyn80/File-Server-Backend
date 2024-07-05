package com.file.server.fileserver.project.data.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.NaturalId;

import java.sql.Date;

@Entity
@Data
@Table(name="users")
public class Users {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private  Long userId;
    @NaturalId(mutable=false)
    private String email;
    private String password;
    private boolean isEnabled=false;
    private String verificationToken;
    private Date tokenExpiry;
    private Role role;
}
