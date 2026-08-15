package com.ammarbhatkar.SPay.bank.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UpiCredentialResponse(
        UUID bankAccountId,
        boolean pinSet,
        Instant pinSetAt
) {
}
