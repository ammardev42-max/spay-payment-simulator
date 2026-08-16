package com.ammarbhatkar.SPay.ledger.entity;

import com.ammarbhatkar.SPay.bank.entity.BankAccount;
import com.ammarbhatkar.SPay.common.enums.LedgerDirection;
import com.ammarbhatkar.SPay.common.enums.LedgerEntryKind;
import com.ammarbhatkar.SPay.payment.entity.PaymentTransaction;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "ledger_entries",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_ledger_deduplication_key", columnNames = "deduplication_key")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedgerEntry {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private PaymentTransaction transaction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LedgerDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LedgerEntryKind entryKind;

    @Column(nullable = false)
    private Long amountPaise;

    @Column(nullable = false)
    private Long balanceAfterPaise;

    @Column(nullable = false, name = "deduplication_key", length = 120)
    private String deduplicationKey;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}