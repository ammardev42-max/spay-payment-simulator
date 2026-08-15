package com.ammarbhatkar.SPay.bank.controller;

import com.ammarbhatkar.SPay.bank.dto.request.StartBankDiscoveryRequest;
import com.ammarbhatkar.SPay.bank.dto.response.BankDiscoveryResponse;
import com.ammarbhatkar.SPay.bank.dto.response.SupportedBankResponse;
import com.ammarbhatkar.SPay.bank.service.BankDiscoveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banks")
@RequiredArgsConstructor
public class BankDiscoveryController {
    private final BankDiscoveryService bankDiscoveryService;

    @GetMapping
    public ResponseEntity<List<SupportedBankResponse>>getSupportedBanks(){
        return ResponseEntity.ok(bankDiscoveryService.getSupportedBanks());
    }
    @PostMapping("/discovery/start")
    public ResponseEntity<BankDiscoveryResponse> startDiscovery(
            @RequestBody @Valid StartBankDiscoveryRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                bankDiscoveryService.startDiscovery(request)
        );
    }
}
