package com.ammarbhatkar.SPay.simulator.entity;

import com.ammarbhatkar.SPay.common.enums.SimulatorMode;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "simulator_rules")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulatorRule {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private SimulatorMode mode;

    @Column(nullable = false)
    private Integer successRate;

    @Column(nullable = false)
    private Integer pendingRate;

    @Column(nullable = false)
    private Boolean timeoutEnabled;

    @Column(nullable = false)
    private Integer maxAttempts;

    @Column(nullable = false)
    private Boolean active;

    @Column(length = 120)
    private String updatedBy;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        updatedAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
