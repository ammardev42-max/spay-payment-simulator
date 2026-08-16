package com.ammarbhatkar.SPay.outbox.service.impl;

import com.ammarbhatkar.SPay.common.enums.OutboxEventStatus;
import com.ammarbhatkar.SPay.outbox.entity.OutboxEvent;
import com.ammarbhatkar.SPay.outbox.repository.OutboxEventRepository;
import com.ammarbhatkar.SPay.outbox.service.OutboxService;
import com.ammarbhatkar.SPay.payment.entity.PaymentTransaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public OutboxEvent savePaymentEvent(PaymentTransaction transaction, String eventType) {
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .eventType(eventType)
                .aggregateType("PAYMENT_TRANSACTION")
                .aggregateId(transaction.getId())
                .payload(toPayload(transaction))
                .status(OutboxEventStatus.PENDING)
                .attempts(0)
                .build();

        return outboxEventRepository.save(outboxEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutboxEvent> findPendingEvents() {
        return outboxEventRepository.findTop50ByStatusInOrderByCreatedAtAsc(List.of(
                OutboxEventStatus.PENDING,
                OutboxEventStatus.FAILED
        ));
    }

    @Override
    @Transactional
    public void markPublished(UUID outboxEventId) {
        OutboxEvent outboxEvent = outboxEventRepository.findById(outboxEventId)
                .orElseThrow(() -> new IllegalStateException("Outbox event not found: " + outboxEventId));

        outboxEvent.setStatus(OutboxEventStatus.PUBLISHED);
        outboxEvent.setPublishedAt(Instant.now());
        outboxEvent.setFailureReason(null);
        outboxEventRepository.save(outboxEvent);
    }

    @Override
    @Transactional
    public void markFailed(UUID outboxEventId, String failureReason) {
        OutboxEvent outboxEvent = outboxEventRepository.findById(outboxEventId)
                .orElseThrow(() -> new IllegalStateException("Outbox event not found: " + outboxEventId));

        outboxEvent.setStatus(OutboxEventStatus.FAILED);
        outboxEvent.setAttempts(outboxEvent.getAttempts() + 1);
        outboxEvent.setFailureReason(failureReason);
        outboxEventRepository.save(outboxEvent);
    }

    private String toPayload(PaymentTransaction transaction) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("paymentId", transaction.getId());
            payload.put("type", transaction.getType().name());
            payload.put("status", transaction.getStatus().name());
            payload.put("senderUpi", transaction.getSenderUpi());
            payload.put("receiverUpi", transaction.getReceiverUpi());
            payload.put("amountPaise", transaction.getAmountPaise());
            payload.put("currency", transaction.getCurrency());
            payload.put("failureCode", transaction.getFailureCode());
            payload.put("failureReason", transaction.getFailureReason());
            payload.put("createdAt", transaction.getCreatedAt());
            payload.put("completedAt", transaction.getCompletedAt());
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize payment outbox payload", exception);
        }
    }
}
