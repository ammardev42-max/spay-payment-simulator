package com.ammarbhatkar.SPay.bank.dto.request;

import jakarta.validation.constraints.NotBlank;

public record StartBankDiscoveryRequest(
        @NotBlank(message = "Bank code is required")
        String bankCode
) {
}