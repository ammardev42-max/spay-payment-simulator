package com.ammarbhatkar.SPay.bank.controller;

import com.ammarbhatkar.SPay.bank.dto.request.SetUpiPinRequest;
import com.ammarbhatkar.SPay.bank.dto.request.VerifyDebitCardRequest;
import com.ammarbhatkar.SPay.bank.dto.response.BankAccountResponse;
import com.ammarbhatkar.SPay.bank.dto.response.UpiCredentialResponse;
import com.ammarbhatkar.SPay.bank.service.BankAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bank-accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @PostMapping("/discovery/{sessionId}/verify-debit-card")
    public ResponseEntity<BankAccountResponse> verifyDebitCard(
            @PathVariable UUID sessionId,
            @RequestBody @Valid VerifyDebitCardRequest request
    ) {
        return ResponseEntity.ok(bankAccountService.verifyDebitCard(sessionId, request));
    }

    @GetMapping("/me")
    public ResponseEntity<List<BankAccountResponse>> getMyBankAccounts() {
        return ResponseEntity.ok(bankAccountService.getMyBankAccounts());
    }

    @PostMapping("/{bankAccountId}/upi-pin")
    public ResponseEntity<UpiCredentialResponse> setUpiPin(
            @PathVariable UUID bankAccountId,
            @RequestBody @Valid SetUpiPinRequest request
    ) {
        return ResponseEntity.ok(bankAccountService.setUpiPin(bankAccountId, request));
    }
}
