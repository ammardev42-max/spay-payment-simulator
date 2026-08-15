package com.ammarbhatkar.SPay.bank.service;

import com.ammarbhatkar.SPay.bank.dto.request.StartBankDiscoveryRequest;
import com.ammarbhatkar.SPay.bank.dto.request.VerifyOtpRequest;
import com.ammarbhatkar.SPay.bank.dto.response.BankDiscoveryResponse;
import com.ammarbhatkar.SPay.bank.dto.response.SupportedBankResponse;
import com.ammarbhatkar.SPay.bank.dto.response.VerifyOtpResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public interface BankDiscoveryService {
    List<SupportedBankResponse> getSupportedBanks();
    BankDiscoveryResponse startDiscovery(StartBankDiscoveryRequest request);
    VerifyOtpResponse verifyOtp(UUID sessionId, VerifyOtpRequest otpRequest);
}
