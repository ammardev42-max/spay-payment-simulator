package com.ammarbhatkar.SPay.payment.dto.request;

import jakarta.validation.constraints.*;

public record CreateUpiPaymentRequest(
        @NotBlank(message = "Sender UPI ID is required")
        String senderUpi,

        @NotBlank(message = "Receiver UPI ID is required")
        String receiverUpi,

        @NotNull(message = "Amount is required")
        @Min(value = 1, message = "Amount must be at least 1 paise")
        @Max(value = 10000000, message = "Amount cannot exceed Rs 1,00,000")
        Long amountPaise,

        @NotBlank(message = "UPI PIN is required")
        @Pattern(regexp = "\\d{4}|\\d{6}", message = "UPI PIN must be 4 or 6 digits")
        String upiPin,

        @Size(max = 200, message = "Note cannot exceed 200 characters")
        String note
) {
}