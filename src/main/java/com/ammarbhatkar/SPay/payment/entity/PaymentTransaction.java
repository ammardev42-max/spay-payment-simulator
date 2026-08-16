package com.ammarbhatkar.SPay.payment.entity;

import com.ammarbhatkar.SPay.bank.entity.BankAccount;
import com.ammarbhatkar.SPay.common.enums.PaymentStatus;
import com.ammarbhatkar.SPay.common.enums.PaymentType;
import com.ammarbhatkar.SPay.user.entity.AppUser;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_id", nullable = false)
    private AppUser senderUser;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_bank_account_id", nullable = false)
    private BankAccount senderBankAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_user_id")
    private AppUser receiverUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_bank_account_id")
    private BankAccount receiverBankAccount;

    @Column(nullable = false, length = 80)
    private String senderUpi;

    @Column(nullable = false, length = 80)
    private String receiverUpi;

    @Column(nullable = false)
    private Long amountPaise;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(length = 200)
    private String note;

    @Column(nullable = false)
    private Integer currentAttempt;

    @Column(length = 80)
    private String failureCode;

    @Column(length = 300)
    private String failureReason;

    private Instant nextRetryAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    private Instant completedAt;

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