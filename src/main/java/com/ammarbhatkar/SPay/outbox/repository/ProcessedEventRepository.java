package com.ammarbhatkar.SPay.outbox.repository;

import com.ammarbhatkar.SPay.outbox.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {

    boolean existsByEventIdAndConsumerName(UUID eventId, String consumerName);

    List<ProcessedEvent> findAllByOrderByProcessedAtDesc();
}
