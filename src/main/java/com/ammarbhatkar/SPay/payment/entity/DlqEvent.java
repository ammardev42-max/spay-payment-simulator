package com.ammarbhatkar.SPay.payment.entity;

import com.ammarbhatkar.SPay.common.enums.DlqStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "dlq_events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DlqEvent {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private PaymentTransaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_attempt_id")
    private PaymentAttempt lastAttempt;

    @Column(length = 120)
    private String sourceEventId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DlqStatus status;

    @Column(nullable = false, length = 80)
    private String reasonCode;

    @Column(nullable = false, length = 300)
    private String reason;

    @Column(nullable = false)
    private Integer retryCount;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant lastRetriedAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}
