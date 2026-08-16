package com.ammarbhatkar.SPay.payment.repository;

import com.ammarbhatkar.SPay.payment.entity.PaymentTimelineEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentTimelineEventRepository extends JpaRepository<PaymentTimelineEvent, UUID> {

    List<PaymentTimelineEvent> findByTransaction_IdOrderByCreatedAtAsc(UUID transactionId);
}