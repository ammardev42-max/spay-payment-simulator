package com.ammarbhatkar.SPay.upi.dto.response;

import java.util.UUID;

public record UpiHandleResponse(
        UUID id,
        UUID bankAccountId,
        String upiId,
        String displayName,
        String bankName,
        String maskedAccountNumber,
        boolean active,
        boolean defaultHandle
) {
}