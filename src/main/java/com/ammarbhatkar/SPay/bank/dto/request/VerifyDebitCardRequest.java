package com.ammarbhatkar.SPay.bank.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyDebitCardRequest(

        @NotBlank(message = "Last six digits are required")
        @Pattern(regexp = "\\d{6}", message = "Last six must contain exactly 6 digits")
        String lastSix,

        @Min(value = 1, message = "Expiry month must be between 1 and 12")
        @Max(value = 12, message = "Expiry month must be between 1 and 12")
        Integer expiryMonth,

        @Min(value = 2026, message = "Expiry year must be valid")
        @Max(value = 2045, message = "Expiry year must be valid")
        Integer expiryYear
) {
}