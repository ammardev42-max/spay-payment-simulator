package com.ammarbhatkar.SPay.payment.dto.response;

import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        String type,
        String status,
        String senderUpi,
        String receiverUpi,
        Long amountPaise,
        String currency,
        String note,
        String failureCode,
        String failureReason,
        Instant createdAt,
        Instant completedAt
) {
}