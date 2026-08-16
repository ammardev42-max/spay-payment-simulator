package com.ammarbhatkar.SPay.payment.service.impl;

import com.ammarbhatkar.SPay.common.enums.IdempotencyStatus;
import com.ammarbhatkar.SPay.common.exception.BusinessRuleViolationException;
import com.ammarbhatkar.SPay.common.exception.DuplicateResourceException;
import com.ammarbhatkar.SPay.common.security.UserContext;
import com.ammarbhatkar.SPay.payment.dto.request.CreateUpiPaymentRequest;
import com.ammarbhatkar.SPay.payment.dto.response.PaymentResponse;
import com.ammarbhatkar.SPay.payment.entity.IdempotencyRecord;
import com.ammarbhatkar.SPay.payment.entity.PaymentTransaction;
import com.ammarbhatkar.SPay.payment.repository.IdempotencyRecordRepository;
import com.ammarbhatkar.SPay.payment.service.IdempotencyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class IdempotencyServiceImpl implements IdempotencyService {

    private static final int IDEMPOTENCY_TTL_HOURS = 24;

    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final UserContext userContext;
    private final ObjectMapper objectMapper;

    @Override
    public String hashUpiPaymentRequest(CreateUpiPaymentRequest request) {
        String normalizedRequest = String.join("|",
                normalize(request.senderUpi()),
                normalize(request.receiverUpi()),
                String.valueOf(request.amountPaise()),
                normalize(request.note())
        );

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(normalizedRequest.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new BusinessRuleViolationException(
                    "REQUEST_HASH_FAILED",
                    "Unable to calculate payment request hash"
            );
        }
    }

    @Override
    public PaymentResponse getExistingPaymentResponse(
            String endpoint,
            String idempotencyKey,
            String requestHash
    ) {
        return idempotencyRecordRepository
                .findByOwnerUser_IdAndEndpointAndIdempotencyKey(
                        userContext.getUserId(),
                        endpoint,
                        idempotencyKey
                )
                .map(record -> toExistingResponse(record, requestHash))
                .orElse(null);
    }

    @Override
    public void savePaymentResponse(
            String endpoint,
            String idempotencyKey,
            String requestHash,
            PaymentTransaction transaction,
            PaymentResponse response
    ) {
        try {
            IdempotencyRecord record = IdempotencyRecord.builder()
                    .ownerUser(transaction.getSenderUser())
                    .transaction(transaction)
                    .endpoint(endpoint)
                    .idempotencyKey(idempotencyKey)
                    .requestHash(requestHash)
                    .responseStatus(HttpStatus.CREATED.value())
                    .responseJson(objectMapper.writeValueAsString(response))
                    .status(IdempotencyStatus.COMPLETED)
                    .expiresAt(Instant.now().plus(IDEMPOTENCY_TTL_HOURS, ChronoUnit.HOURS))
                    .build();

            idempotencyRecordRepository.save(record);
        } catch (JsonProcessingException exception) {
            throw new BusinessRuleViolationException(
                    "IDEMPOTENCY_RESPONSE_SERIALIZATION_FAILED",
                    "Unable to save idempotent payment response"
            );
        }
    }

    private PaymentResponse toExistingResponse(IdempotencyRecord record, String requestHash) {
        if (!record.getRequestHash().equals(requestHash)) {
            throw new DuplicateResourceException(
                    "IDEMPOTENCY_KEY_REUSED",
                    "This idempotency key was already used for a different payment request"
            );
        }

        try {
            return objectMapper.readValue(record.getResponseJson(), PaymentResponse.class);
        } catch (JsonProcessingException exception) {
            throw new BusinessRuleViolationException(
                    "IDEMPOTENCY_RESPONSE_READ_FAILED",
                    "Unable to read saved idempotent payment response"
            );
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value.trim().toLowerCase(Locale.ROOT);
    }
}
