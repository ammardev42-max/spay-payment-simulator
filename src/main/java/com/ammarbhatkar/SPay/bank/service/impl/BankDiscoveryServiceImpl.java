package com.ammarbhatkar.SPay.bank.service.impl;

import com.ammarbhatkar.SPay.bank.dto.request.StartBankDiscoveryRequest;
import com.ammarbhatkar.SPay.bank.dto.response.BankDiscoveryResponse;
import com.ammarbhatkar.SPay.bank.dto.response.SupportedBankResponse;
import com.ammarbhatkar.SPay.bank.entity.BankDiscoverySession;
import com.ammarbhatkar.SPay.bank.repository.BankDiscoverySessionRepository;
import com.ammarbhatkar.SPay.bank.service.BankDiscoveryService;
import com.ammarbhatkar.SPay.common.enums.BankDiscoveryStatus;
import com.ammarbhatkar.SPay.common.exception.BusinessRuleViolationException;
import com.ammarbhatkar.SPay.common.exception.ResourceNotFoundException;
import com.ammarbhatkar.SPay.user.entity.AppUser;
import com.ammarbhatkar.SPay.user.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisSubscribedConnectionException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static java.security.CryptoPrimitive.SECURE_RANDOM;

@Service
@RequiredArgsConstructor
public class BankDiscoveryServiceImpl implements BankDiscoveryService {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final AppUserRepository appUserRepository;
    private final BankDiscoverySessionRepository bankDiscoverySessionRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Map<String, String> SUPPORTED_BANKS = Map.of(
            "HDFC", "HDFC Bank",
            "SBI", "State Bank of India",
            "ICICI", "ICICI Bank",
            "AXIS", "Axis Bank",
            "KOTAK", "Kotak Mahindra Bank"
    );



    @Override
    public List<SupportedBankResponse> getSupportedBanks() {
        return SUPPORTED_BANKS.entrySet()
                .stream()
                .map(entry-> new SupportedBankResponse(entry.getKey(),entry.getValue()))
                .toList();
    }

    @Override
    @Transactional
    public BankDiscoveryResponse startDiscovery(StartBankDiscoveryRequest request) {
        AppUser user = getCurrentUser();
        String bankCode = request.bankCode().trim().toUpperCase(Locale.ROOT);

        if (!SUPPORTED_BANKS.containsKey(bankCode)) {
            throw new BusinessRuleViolationException(
                    "UNSUPPORTED_BANK",
                    "Unsupported bank: " + bankCode
            );
        }

        String otp = String.valueOf(100000 + SECURE_RANDOM.nextInt(900000));
        String lastFour = user.getPhoneNumber().substring(user.getPhoneNumber().length() - 4);

        BankDiscoverySession session = BankDiscoverySession.builder()
                .user(user)
                .bankCode(bankCode)
                .maskedAccountNumber("XXXXXX" + lastFour)
                .ifsc(bankCode + "0001234")
                .accountToken(UUID.randomUUID().toString())
                .otpHash(passwordEncoder.encode(otp))
                .status(BankDiscoveryStatus.OTP_SENT)
                .otpAttempts(0)
                .expiresAt(Instant.now().plusSeconds(300))
                .build();

        BankDiscoverySession savedSession = bankDiscoverySessionRepository.save(session);

        return new BankDiscoveryResponse(
                savedSession.getId(),
                savedSession.getBankCode(),
                savedSession.getMaskedAccountNumber(),
                savedSession.getIfsc(),
                savedSession.getStatus().name(),
                savedSession.getExpiresAt(),
                otp
        );
    }

    private AppUser getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }
}
