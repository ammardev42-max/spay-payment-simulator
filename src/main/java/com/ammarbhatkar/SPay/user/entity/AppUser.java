package com.ammarbhatkar.SPay.user.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false,length = 130)
    private String fullName;

    @Column(nullable = false,unique = true,length = 160)
    private String email;

    @Column(nullable = false,unique = true,length = 10)
    private String phoneNumber;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false,length = 30)
    private String role;

    @Column(nullable = false, length = 30)
    private String status;


    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;
}
