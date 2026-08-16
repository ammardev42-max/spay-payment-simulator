package com.ammarbhatkar.SPay.admin.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ProcessedEventResponse(
        UUID id,
        UUID eventId,
        String eventType,
        String consumerName,
        Instant processedAt
) {
}
