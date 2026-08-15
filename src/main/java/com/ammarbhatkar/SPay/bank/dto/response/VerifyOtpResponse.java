package com.ammarbhatkar.SPay.bank.dto.response;

import java.util.UUID;

public record VerifyOtpResponse(
        UUID discoverySessionId,
        String status,
        String message
) {
}