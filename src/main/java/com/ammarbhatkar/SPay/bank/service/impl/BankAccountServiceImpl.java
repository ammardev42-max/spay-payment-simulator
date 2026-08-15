package com.ammarbhatkar.SPay.bank.service.impl;

import com.ammarbhatkar.SPay.bank.dto.request.VerifyDebitCardRequest;
import com.ammarbhatkar.SPay.bank.dto.response.BankAccountResponse;
import com.ammarbhatkar.SPay.bank.repository.BankAccountRepository;
import com.ammarbhatkar.SPay.bank.repository.BankDiscoverySessionRepository;
import com.ammarbhatkar.SPay.bank.service.BankAccountService;
import com.ammarbhatkar.SPay.common.exception.BusinessRuleViolationException;
import com.ammarbhatkar.SPay.user.entity.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {
    private final BankDiscoverySessionRepository bankDiscoverySessionRepository;
    private final BankAccountRepository bankAccountRepository;
    @Override
    public BankAccountResponse verifyDebitCard(UUID discoverySessionId, VerifyDebitCardRequest request) {
        return null;
    }

    @Override
    public List<BankAccountResponse> getMyBankAccounts() {
        AppUser user = getCurrentUser();

        return bankAccountRepository.findByUser(user)
                .stream()
                .map(this::toResponse)
                .orElseGet(() -> createBankAccount(session));
    }

    private AppUser getCurrentUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof AppUser appUser) {
            return appUser;
        }

        throw new BusinessRuleViolationException(
                "AUTHENTICATED_USER_REQUIRED",
                "Authenticated user is required"
        );
    }
}
