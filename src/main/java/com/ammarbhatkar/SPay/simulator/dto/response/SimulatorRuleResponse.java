package com.ammarbhatkar.SPay.simulator.dto.response;

import java.time.Instant;
import java.util.UUID;

public record SimulatorRuleResponse(
        UUID id,
        String mode,
        Integer successRate,
        Integer pendingRate,
        Boolean timeoutEnabled,
        Integer maxAttempts,
        Boolean active,
        String updatedBy,
        Instant updatedAt
) {
}
