package com.ammarbhatkar.SPay.bank.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "upi_credentials")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpiCredential {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false, unique = true)
    private BankAccount bankAccount;

    @Column(nullable = false)
    private String upiPinHash;

    @Column(nullable = false)
    private Integer failedAttempts;

    private Instant lockedUntil;

    @Column(nullable = false)
    private Instant pinSetAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        pinSetAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
