package com.ammarbhatkar.SPay.admin.dto.response;

import java.time.Instant;
import java.util.UUID;

public record DlqEventResponse(
        UUID id,
        UUID transactionId,
        UUID lastAttemptId,
        String status,
        String reasonCode,
        String reason,
        Integer retryCount,
        Instant createdAt,
        Instant lastRetriedAt
) {
}
