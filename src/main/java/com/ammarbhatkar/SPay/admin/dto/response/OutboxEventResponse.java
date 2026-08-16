package com.ammarbhatkar.SPay.admin.dto.response;

import java.time.Instant;
import java.util.UUID;

public record OutboxEventResponse(
        UUID id,
        String eventType,
        String aggregateType,
        UUID aggregateId,
        String status,
        Integer attempts,
        String failureReason,
        Instant createdAt,
        Instant publishedAt
) {
}
