package com.ammarbhatkar.SPay.bank.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record VerifyOtpRequest(

        @NotBlank(message = "OTP is required")
        @NotNull
        @Pattern(regexp = "\\d{6}", message = "OTP must be 6 digits")
        String otp
) {

}
