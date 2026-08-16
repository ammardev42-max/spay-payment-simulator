package com.ammarbhatkar.SPay.upi.service.impl;

import com.ammarbhatkar.SPay.bank.entity.BankAccount;
import com.ammarbhatkar.SPay.bank.repository.BankAccountRepository;
import com.ammarbhatkar.SPay.bank.repository.UpiCredentialRepository;
import com.ammarbhatkar.SPay.common.enums.BankAccountStatus;
import com.ammarbhatkar.SPay.common.enums.UpiHandleStatus;
import com.ammarbhatkar.SPay.common.exception.BusinessRuleViolationException;
import com.ammarbhatkar.SPay.common.exception.DuplicateResourceException;
import com.ammarbhatkar.SPay.common.exception.ResourceNotFoundException;
import com.ammarbhatkar.SPay.common.security.UserContext;
import com.ammarbhatkar.SPay.upi.dto.request.CreateUpiHandleRequest;
import com.ammarbhatkar.SPay.upi.dto.response.UpiHandleResponse;
import com.ammarbhatkar.SPay.upi.entity.UpiHandle;
import com.ammarbhatkar.SPay.upi.mapper.UpiHandleMapper;
import com.ammarbhatkar.SPay.upi.repository.UpiHandleRepository;
import com.ammarbhatkar.SPay.upi.service.UpiResolveCacheService;
import com.ammarbhatkar.SPay.upi.service.UpiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UpiServiceImpl implements UpiService {

    private final BankAccountRepository bankAccountRepository;
    private final UpiCredentialRepository upiCredentialRepository;
    private final UpiHandleRepository upiHandleRepository;
    private final UpiHandleMapper upiHandleMapper;
    private final UserContext userContext;
    private final UpiResolveCacheService upiResolveCacheService;

    @Override
    @Transactional
    public UpiHandleResponse createHandle(CreateUpiHandleRequest request) {
        String upiId = request.upiId().trim().toLowerCase(Locale.ROOT);

        if (upiHandleRepository.existsByUpiId(upiId)) {
            throw new DuplicateResourceException(
                    "DUPLICATE_UPI_ID",
                    "UPI ID already exists: " + upiId
            );
        }

        BankAccount bankAccount = bankAccountRepository.findById(request.bankAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("BankAccount", request.bankAccountId().toString()));

        validateBankAccountForUpi(bankAccount);

        boolean defaultHandle = !upiHandleRepository.existsByUser_IdAndDefaultHandleTrue(userContext.getUserId());

        UpiHandle upiHandle = UpiHandle.builder()
                .user(bankAccount.getUser())
                .bankAccount(bankAccount)
                .upiId(upiId)
                .status(UpiHandleStatus.ACTIVE)
                .defaultHandle(defaultHandle)
                .build();

        UpiHandle savedHandle = upiHandleRepository.save(upiHandle);

        UpiHandleResponse response = upiHandleMapper.toResponse(savedHandle);
        upiResolveCacheService.put(upiId, response);

        return response;
    }

    @Override
    public List<UpiHandleResponse> getMyHandles() {
        return upiHandleMapper.toResponseList(
                upiHandleRepository.findByUser_Id(userContext.getUserId())
        );
    }

    @Override
    public UpiHandleResponse resolve(String upiId) {
        String normalizedUpiId = upiId.trim().toLowerCase(Locale.ROOT);

        UpiHandleResponse cachedResponse = upiResolveCacheService.get(normalizedUpiId)
                .orElse(null);

        if (cachedResponse != null) {
            return cachedResponse;
        }

        UpiHandle upiHandle = upiHandleRepository.findByUpiIdAndStatus(normalizedUpiId, UpiHandleStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("UpiHandle", normalizedUpiId));

        UpiHandleResponse response = upiHandleMapper.toResponse(upiHandle);
        upiResolveCacheService.put(normalizedUpiId, response);

        return response;
    }

    private void validateBankAccountForUpi(BankAccount bankAccount) {
        if (!bankAccount.getUser().getId().equals(userContext.getUserId())) {
            throw new BusinessRuleViolationException(
                    "BANK_ACCOUNT_ACCESS_DENIED",
                    "You cannot create UPI ID for this bank account"
            );
        }

        if (bankAccount.getStatus() != BankAccountStatus.VERIFIED) {
            throw new BusinessRuleViolationException(
                    "BANK_ACCOUNT_NOT_VERIFIED",
                    "Bank account must be verified before creating UPI ID"
            );
        }

        if (!upiCredentialRepository.existsByBankAccount_Id(bankAccount.getId())) {
            throw new BusinessRuleViolationException(
                    "UPI_PIN_NOT_SET",
                    "UPI PIN must be set before creating UPI ID"
            );
        }
    }
}
