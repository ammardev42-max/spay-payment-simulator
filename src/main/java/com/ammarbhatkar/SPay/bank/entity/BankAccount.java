package com.ammarbhatkar.SPay.bank.entity;

import com.ammarbhatkar.SPay.common.enums.BankAccountStatus;
import com.ammarbhatkar.SPay.user.entity.AppUser;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bank_accounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private AppUser user;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private BankDiscoverySession discoverySession;

    @Column(nullable = false, length = 20)
    private String bankCode;

    @Column(nullable = false, length = 80)
    private String bankName;

    @Column(nullable = false, unique = true, length = 80)
    private String accountToken;

    @Column(nullable = false, length = 20)
    private String maskedAccountNumber;

    @Column(nullable = false, length = 20)
    private String ifsc;

    @Column(nullable = false)
    private Long balancePaise;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BankAccountStatus status;

    private Instant verifiedAt;

    @Version
    private Integer version;

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