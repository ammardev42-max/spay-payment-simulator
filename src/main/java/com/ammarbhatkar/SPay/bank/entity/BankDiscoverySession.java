package com.ammarbhatkar.SPay.bank.entity;

import com.ammarbhatkar.SPay.common.enums.BankDiscoveryStatus;
import com.ammarbhatkar.SPay.user.entity.AppUser;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bank_discovery_sessions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankDiscoverySession {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false, length = 30)
    private String bankCode;

    @Column(nullable = false, length = 20)
    private String maskedAccountNumber;

    @Column(nullable = false, length = 20)
    private String ifsc;

    @Column(nullable = false, unique = true, length = 80)
    private String accountToken;

    @Column(nullable = false)
    private String otpHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BankDiscoveryStatus status;

    @Column(nullable = false)
    private int otpAttempts;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant otpVerifiedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}