package com.ammarbhatkar.SPay.bank.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SetUpiPinRequest(

        @NotBlank(message = "UPI PIN is required")
        @Pattern(regexp = "\\d{4}|\\d{6}", message = "UPI PIN must be 4 or 6 digits")
        String upiPin,

        @NotBlank(message = "Confirm UPI PIN is required")
        @Pattern(regexp = "\\d{4}|\\d{6}", message = "Confirm UPI PIN must be 4 or 6 digits")
        String confirmUpiPin
) {
}
