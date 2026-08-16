package com.ammarbhatkar.SPay.upi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record CreateUpiHandleRequest(

        @NotNull(message = "Bank account ID is required")
        UUID bankAccountId,

        @NotBlank(message = "UPI ID is required")
        @Pattern(
                regexp = "^[a-z0-9][a-z0-9._-]{2,29}@spay$",
                message = "UPI ID must be 3-30 characters followed by @spay"
        )
        String upiId
) {
}