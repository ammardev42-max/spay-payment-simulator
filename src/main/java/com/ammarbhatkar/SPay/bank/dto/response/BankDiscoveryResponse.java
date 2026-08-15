package com.ammarbhatkar.SPay.bank.dto.response;

import java.time.Instant;
import java.util.UUID;

public record BankDiscoveryResponse(
        UUID discoverySessionId,
        String bankCode,
        String maskedAccountNumber,
        String ifsc,
        String status,
        Instant expiresAt,
        String demoOtp
) {
}