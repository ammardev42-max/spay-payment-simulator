package com.ammarbhatkar.SPay.bank.service;

import com.ammarbhatkar.SPay.bank.dto.request.StartBankDiscoveryRequest;
import com.ammarbhatkar.SPay.bank.dto.response.BankDiscoveryResponse;
import com.ammarbhatkar.SPay.bank.dto.response.SupportedBankResponse;
import org.springframework.stereotype.Service;

import java.util.List;

public interface BankDiscoveryService {
    List<SupportedBankResponse> getSupportedBanks();
    BankDiscoveryResponse startDiscovery(StartBankDiscoveryRequest request);

}
