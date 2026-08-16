package com.ammarbhatkar.SPay.payment.dto.response;

import java.time.Instant;
import java.util.UUID;

public record PaymentAttemptResponse(
        UUID id,
        UUID transactionId,
        Integer attemptNumber,
        String outcome,
        Boolean retryable,
        String failureCode,
        String failureReason,
        Instant startedAt,
        Instant completedAt,
        Instant nextRetryAt
) {
}
