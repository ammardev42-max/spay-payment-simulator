package com.ammarbhatkar.SPay.bank.service;

import com.ammarbhatkar.SPay.bank.dto.request.SetUpiPinRequest;
import com.ammarbhatkar.SPay.bank.dto.request.VerifyDebitCardRequest;
import com.ammarbhatkar.SPay.bank.dto.response.BankAccountResponse;
import com.ammarbhatkar.SPay.bank.dto.response.UpiCredentialResponse;

import java.util.List;
import java.util.UUID;

public interface BankAccountService {

    BankAccountResponse verifyDebitCard(UUID discoverySessionId, VerifyDebitCardRequest request);

    List<BankAccountResponse> getMyBankAccounts();

    UpiCredentialResponse setUpiPin(UUID bankAccountId, SetUpiPinRequest request);
}
