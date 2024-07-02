package com.file.server.fileserver.project.data.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;

import java.time.LocalDate;

@Data
@ToString

@Entity
@Table
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NaturalId(mutable = true)
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean isEnabled = false;

    @Column(nullable = true)
    private String verificationToken;

    private LocalDate verificationExpiry;
}
