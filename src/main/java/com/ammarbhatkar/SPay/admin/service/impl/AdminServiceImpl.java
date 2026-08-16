package com.ammarbhatkar.SPay.admin.service.impl;

import com.ammarbhatkar.SPay.admin.dto.response.DlqEventResponse;
import com.ammarbhatkar.SPay.admin.dto.response.OutboxEventResponse;
import com.ammarbhatkar.SPay.admin.dto.response.ProcessedEventResponse;
import com.ammarbhatkar.SPay.admin.service.AdminService;
import com.ammarbhatkar.SPay.payment.entity.DlqEvent;
import com.ammarbhatkar.SPay.payment.repository.DlqEventRepository;
import com.ammarbhatkar.SPay.outbox.entity.OutboxEvent;
import com.ammarbhatkar.SPay.outbox.entity.ProcessedEvent;
import com.ammarbhatkar.SPay.outbox.repository.OutboxEventRepository;
import com.ammarbhatkar.SPay.outbox.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final DlqEventRepository dlqEventRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ProcessedEventRepository processedEventRepository;

    @Override
    public List<DlqEventResponse> getDlqEvents() {
        return dlqEventRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<OutboxEventResponse> getOutboxEvents() {
        return outboxEventRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toOutboxResponse)
                .toList();
    }

    @Override
    public List<ProcessedEventResponse> getProcessedEvents() {
        return processedEventRepository.findAllByOrderByProcessedAtDesc()
                .stream()
                .map(this::toProcessedEventResponse)
                .toList();
    }

    private DlqEventResponse toResponse(DlqEvent dlqEvent) {
        return new DlqEventResponse(
                dlqEvent.getId(),
                dlqEvent.getTransaction().getId(),
                dlqEvent.getLastAttempt() == null ? null : dlqEvent.getLastAttempt().getId(),
                dlqEvent.getStatus().name(),
                dlqEvent.getReasonCode(),
                dlqEvent.getReason(),
                dlqEvent.getRetryCount(),
                dlqEvent.getCreatedAt(),
                dlqEvent.getLastRetriedAt()
        );
    }

    private OutboxEventResponse toOutboxResponse(OutboxEvent outboxEvent) {
        return new OutboxEventResponse(
                outboxEvent.getId(),
                outboxEvent.getEventType(),
                outboxEvent.getAggregateType(),
                outboxEvent.getAggregateId(),
                outboxEvent.getStatus().name(),
                outboxEvent.getAttempts(),
                outboxEvent.getFailureReason(),
                outboxEvent.getCreatedAt(),
                outboxEvent.getPublishedAt()
        );
    }

    private ProcessedEventResponse toProcessedEventResponse(ProcessedEvent processedEvent) {
        return new ProcessedEventResponse(
                processedEvent.getId(),
                processedEvent.getEventId(),
                processedEvent.getEventType(),
                processedEvent.getConsumerName(),
                processedEvent.getProcessedAt()
        );
    }
}
