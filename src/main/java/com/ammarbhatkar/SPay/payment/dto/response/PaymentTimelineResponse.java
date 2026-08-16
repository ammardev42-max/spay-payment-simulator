package com.ammarbhatkar.SPay.payment.dto.response;

import java.time.Instant;
import java.util.UUID;

public record PaymentTimelineResponse(
        UUID id,
        String status,
        String message,
        Instant createdAt
) {
}