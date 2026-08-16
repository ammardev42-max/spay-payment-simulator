package com.ammarbhatkar.SPay.payment.entity;

import com.ammarbhatkar.SPay.common.enums.PaymentAttemptOutcome;
import com.ammarbhatkar.SPay.simulator.entity.SimulatorRule;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "payment_attempts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_payment_attempt_transaction_attempt",
                        columnNames = {"transaction_id", "attempt_number"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAttempt {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private PaymentTransaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulator_rule_id")
    private SimulatorRule simulatorRule;

    @Column(nullable = false)
    private Integer attemptNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private PaymentAttemptOutcome outcome;

    @Column(nullable = false)
    private Boolean retryable;

    @Column(length = 80)
    private String failureCode;

    @Column(length = 300)
    private String failureReason;

    @Column(nullable = false, updatable = false)
    private Instant startedAt;

    private Instant completedAt;

    private Instant nextRetryAt;

    @PrePersist
    void onCreate() {
        if (startedAt == null) {
            startedAt = Instant.now();
        }
    }
}
