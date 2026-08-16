package com.ammarbhatkar.SPay.upi.entity;

import com.ammarbhatkar.SPay.bank.entity.BankAccount;
import com.ammarbhatkar.SPay.common.enums.UpiHandleStatus;
import com.ammarbhatkar.SPay.user.entity.AppUser;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "upi_handles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpiHandle {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;

    @Column(nullable = false, unique = true, length = 80)
    private String upiId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UpiHandleStatus status;

    @Column(nullable = false)
    private Boolean defaultHandle;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}