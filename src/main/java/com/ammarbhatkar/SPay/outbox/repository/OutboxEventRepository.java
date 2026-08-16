package com.ammarbhatkar.SPay.outbox.repository;

import com.ammarbhatkar.SPay.common.enums.OutboxEventStatus;
import com.ammarbhatkar.SPay.outbox.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findTop50ByStatusOrderByCreatedAtAsc(OutboxEventStatus status);

    List<OutboxEvent> findTop50ByStatusInOrderByCreatedAtAsc(Collection<OutboxEventStatus> statuses);

    List<OutboxEvent> findAllByOrderByCreatedAtDesc();
}
