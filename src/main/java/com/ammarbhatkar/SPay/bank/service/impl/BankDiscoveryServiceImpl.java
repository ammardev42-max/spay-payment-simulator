package com.ammarbhatkar.SPay.bank.service.impl;

import com.ammarbhatkar.SPay.bank.dto.request.StartBankDiscoveryRequest;
import com.ammarbhatkar.SPay.bank.dto.request.VerifyOtpRequest;
import com.ammarbhatkar.SPay.bank.dto.response.BankDiscoveryResponse;
import com.ammarbhatkar.SPay.bank.dto.response.SupportedBankResponse;
import com.ammarbhatkar.SPay.bank.dto.response.VerifyOtpResponse;
import com.ammarbhatkar.SPay.bank.entity.BankDiscoverySession;
import com.ammarbhatkar.SPay.bank.repository.BankDiscoverySessionRepository;
import com.ammarbhatkar.SPay.bank.service.BankDiscoveryService;
import com.ammarbhatkar.SPay.common.enums.BankDiscoveryStatus;
import com.ammarbhatkar.SPay.common.exception.BusinessRuleViolationException;
import com.ammarbhatkar.SPay.common.exception.ResourceNotFoundException;
import com.ammarbhatkar.SPay.common.security.UserContext;
import com.ammarbhatkar.SPay.user.entity.AppUser;
import com.ammarbhatkar.SPay.user.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.connection.RedisSubscribedConnectionException;
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
    private final UserContext userContext;

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
        AppUser user = appUserRepository.findById(userContext.getUserId())
                   .orElseThrow(() -> new ResourceNotFoundException(
                                     "User",
                                     userContext.getUserId().toString()
                           ));        String bankCode = request.bankCode().trim().toUpperCase(Locale.ROOT);

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

    @Override
    @Transactional
    public VerifyOtpResponse verifyOtp(UUID sessionId, VerifyOtpRequest otpRequest) {
        BankDiscoverySession session = bankDiscoverySessionRepository.findById(sessionId)
                   .orElseThrow(() -> new ResourceNotFoundException("BankDiscoverySession", sessionId.toString()));

            if(session.getStatus()!=BankDiscoveryStatus.OTP_SENT){
                throw new BusinessRuleViolationException(
                        "INVALID_DISCOVERY_SESSION_STATE",
                        "OTP cannot be verified for current session state: " + session.getStatus()
                );
            }
        if (session.getExpiresAt().isBefore(Instant.now())) {
                     session.setStatus(BankDiscoveryStatus.EXPIRED);
                     bankDiscoverySessionRepository.save(session);
                     throw new BusinessRuleViolationException(
                                     "OTP_EXPIRED",
                                     "OTP has expired. Please start bank discovery again."
                             );
                 }

             if (session.getOtpAttempts() >= 3) {
                     session.setStatus(BankDiscoveryStatus.EXPIRED);
                     bankDiscoverySessionRepository.save(session);

                     throw new BusinessRuleViolationException(
                                     "OTP_ATTEMPTS_EXCEEDED",
                                     "Too many wrong OTP attempts. Please start bank discovery again."
                             );
                 }
             boolean otpMatches = passwordEncoder.matches(
                     otpRequest.otp(),
                     session.getOtpHash()
             );
        if (!otpMatches) {
                     session.setOtpAttempts(session.getOtpAttempts() + 1);
                     bankDiscoverySessionRepository.save(session);

                     throw new BusinessRuleViolationException(
                                     "INVALID_OTP",
                                     "Invalid OTP"
                     );
                 }
        session.setStatus(BankDiscoveryStatus.OTP_VERIFIED);
        session.setOtpVerifiedAt(Instant.now());
        bankDiscoverySessionRepository.save(session);
        return new VerifyOtpResponse(
                session.getId(),
                session.getStatus().name(),
                "OTP verified successfully"
        );
    }

}
