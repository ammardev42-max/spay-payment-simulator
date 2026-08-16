package com.ammarbhatkar.SPay.outbox.service;

import com.ammarbhatkar.SPay.payment.entity.PaymentTransaction;
import com.ammarbhatkar.SPay.outbox.entity.OutboxEvent;

import java.util.List;
import java.util.UUID;

public interface OutboxService {

    OutboxEvent savePaymentEvent(PaymentTransaction transaction, String eventType);

    List<OutboxEvent> findPendingEvents();

    void markPublished(UUID outboxEventId);

    void markFailed(UUID outboxEventId, String failureReason);
}
