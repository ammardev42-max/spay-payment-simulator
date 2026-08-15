package com.ammarbhatkar.SPay.bank.dto.response;

import java.util.UUID;

public record BankAccountResponse(
        UUID id,
        String bankCode,
        String bankName,
        String maskedAccountNumber,
        String ifsc,
        Long balancePaise,
        String status
) {
}