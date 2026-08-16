package com.ammarbhatkar.SPay.simulator.dto.request;

import com.ammarbhatkar.SPay.common.enums.SimulatorMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateSimulatorRuleRequest(
        @NotNull(message = "Simulator mode is required")
        SimulatorMode mode,

        @Min(value = 1, message = "Max attempts must be at least 1")
        @Max(value = 5, message = "Max attempts cannot exceed 5")
        Integer maxAttempts
) {
}
