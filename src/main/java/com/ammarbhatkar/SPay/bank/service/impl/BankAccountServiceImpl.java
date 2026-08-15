package com.ammarbhatkar.SPay.bank.service.impl;

import com.ammarbhatkar.SPay.bank.dto.request.SetUpiPinRequest;
import com.ammarbhatkar.SPay.bank.dto.request.VerifyDebitCardRequest;
import com.ammarbhatkar.SPay.bank.dto.response.BankAccountResponse;
import com.ammarbhatkar.SPay.bank.dto.response.UpiCredentialResponse;
import com.ammarbhatkar.SPay.bank.entity.BankAccount;
import com.ammarbhatkar.SPay.bank.entity.BankDiscoverySession;
import com.ammarbhatkar.SPay.bank.entity.UpiCredential;
import com.ammarbhatkar.SPay.bank.mapper.BankAccountMapper;
import com.ammarbhatkar.SPay.bank.repository.BankAccountRepository;
import com.ammarbhatkar.SPay.bank.repository.BankDiscoverySessionRepository;
import com.ammarbhatkar.SPay.bank.repository.UpiCredentialRepository;
import com.ammarbhatkar.SPay.bank.service.BankAccountService;
import com.ammarbhatkar.SPay.common.enums.BankAccountStatus;
import com.ammarbhatkar.SPay.common.enums.BankDiscoveryStatus;
import com.ammarbhatkar.SPay.common.exception.BusinessRuleViolationException;
import com.ammarbhatkar.SPay.common.exception.ResourceNotFoundException;
import com.ammarbhatkar.SPay.common.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BankAccountServiceImpl implements BankAccountService {

    private static final Long DEMO_BALANCE_PAISE = 1_000_000L;

    private final BankDiscoverySessionRepository bankDiscoverySessionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final UpiCredentialRepository upiCredentialRepository;
    private final BankAccountMapper bankAccountMapper;
    private final UserContext userContext;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public BankAccountResponse verifyDebitCard(UUID discoverySessionId, VerifyDebitCardRequest request) {
        BankDiscoverySession session = bankDiscoverySessionRepository.findById(discoverySessionId)
                .orElseThrow(() -> new ResourceNotFoundException("BankDiscoverySession", discoverySessionId.toString()));

        validateDiscoverySession(session);
        validateDebitCard(request);

        BankAccount bankAccount = bankAccountRepository.findByDiscoverySession(session)
                .orElseGet(() -> createBankAccount(session));

        return bankAccountMapper.toResponse(bankAccount);
    }

    @Override
    public List<BankAccountResponse> getMyBankAccounts() {
        return bankAccountMapper.toResponseList(
                bankAccountRepository.findByUser_Id(userContext.getUserId())
        );
    }

    @Override
    @Transactional
    public UpiCredentialResponse setUpiPin(UUID bankAccountId, SetUpiPinRequest request) {
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("BankAccount", bankAccountId.toString()));

        if (!bankAccount.getUser().getId().equals(userContext.getUserId())) {
            throw new BusinessRuleViolationException(
                    "BANK_ACCOUNT_ACCESS_DENIED",
                    "You cannot set UPI PIN for this bank account"
            );
        }

        if (bankAccount.getStatus() != BankAccountStatus.VERIFIED) {
            throw new BusinessRuleViolationException(
                    "BANK_ACCOUNT_NOT_VERIFIED",
                    "Bank account must be verified before setting UPI PIN"
            );
        }

        if (!request.upiPin().equals(request.confirmUpiPin())) {
            throw new BusinessRuleViolationException(
                    "UPI_PIN_MISMATCH",
                    "UPI PIN and confirm UPI PIN must match"
            );
        }

        if (upiCredentialRepository.existsByBankAccount_Id(bankAccountId)) {
            throw new BusinessRuleViolationException(
                    "UPI_PIN_ALREADY_SET",
                    "UPI PIN is already set for this bank account"
            );
        }

        UpiCredential credential = UpiCredential.builder()
                .bankAccount(bankAccount)
                .upiPinHash(passwordEncoder.encode(request.upiPin()))
                .failedAttempts(0)
                .build();

        UpiCredential savedCredential = upiCredentialRepository.save(credential);

        return new UpiCredentialResponse(
                bankAccount.getId(),
                true,
                savedCredential.getPinSetAt()
        );
    }

    private void validateDiscoverySession(BankDiscoverySession session) {
        if (!session.getUser().getId().equals(userContext.getUserId())) {
            throw new BusinessRuleViolationException(
                    "BANK_DISCOVERY_ACCESS_DENIED",
                    "You cannot verify this bank discovery session"
            );
        }

        if (session.getStatus() != BankDiscoveryStatus.OTP_VERIFIED) {
            throw new BusinessRuleViolationException(
                    "OTP_NOT_VERIFIED",
                    "OTP must be verified before debit card verification"
            );
        }
    }

    private void validateDebitCard(VerifyDebitCardRequest request) {
        if ("000000".equals(request.lastSix())) {
            throw new BusinessRuleViolationException(
                    "DEBIT_CARD_VERIFICATION_FAILED",
                    "Debit card verification failed"
            );
        }

        YearMonth expiry = YearMonth.of(request.expiryYear(), request.expiryMonth());

        if (expiry.isBefore(YearMonth.now())) {
            throw new BusinessRuleViolationException(
                    "DEBIT_CARD_EXPIRED",
                    "Debit card is expired"
            );
        }
    }

    private BankAccount createBankAccount(BankDiscoverySession session) {
        BankAccount bankAccount = BankAccount.builder()
                .user(session.getUser())
                .discoverySession(session)
                .bankCode(session.getBankCode())
                .bankName(session.getBankName())
                .accountToken(session.getAccountToken())
                .maskedAccountNumber(session.getMaskedAccountNumber())
                .ifsc(session.getIfsc())
                .balancePaise(DEMO_BALANCE_PAISE)
                .status(BankAccountStatus.VERIFIED)
                .verifiedAt(Instant.now())
                .build();

        return bankAccountRepository.save(bankAccount);
    }
}
