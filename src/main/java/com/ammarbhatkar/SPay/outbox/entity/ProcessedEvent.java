package com.ammarbhatkar.SPay.outbox.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "processed_events",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_processed_event_event_id", columnNames = "event_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedEvent {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(nullable = false, length = 80)
    private String eventType;

    @Column(nullable = false, length = 80)
    private String consumerName;

    @Column(nullable = false)
    private Instant processedAt;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;
}
