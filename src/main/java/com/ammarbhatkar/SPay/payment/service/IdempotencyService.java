package com.ammarbhatkar.SPay.payment.service;

import com.ammarbhatkar.SPay.payment.dto.request.CreateUpiPaymentRequest;
import com.ammarbhatkar.SPay.payment.dto.response.PaymentResponse;
import com.ammarbhatkar.SPay.payment.entity.PaymentTransaction;

public interface IdempotencyService {

    String hashUpiPaymentRequest(CreateUpiPaymentRequest request);

    PaymentResponse getExistingPaymentResponse(
            String endpoint,
            String idempotencyKey,
            String requestHash
    );

    void savePaymentResponse(
            String endpoint,
            String idempotencyKey,
            String requestHash,
            PaymentTransaction transaction,
            PaymentResponse response
    );
}
