package com.ammarbhatkar.SPay.payment.service;

import com.ammarbhatkar.SPay.payment.dto.request.CreateUpiPaymentRequest;
import com.ammarbhatkar.SPay.payment.dto.response.PaymentAttemptResponse;
import com.ammarbhatkar.SPay.payment.dto.response.PaymentResponse;
import com.ammarbhatkar.SPay.payment.dto.response.PaymentTimelineResponse;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentResponse createUpiPayment(CreateUpiPaymentRequest request, String idempotencyKey);

    PaymentResponse getPayment(UUID paymentId);

    List<PaymentTimelineResponse> getTimeline(UUID paymentId);

    List<PaymentAttemptResponse> getAttempts(UUID paymentId);

    List<PaymentResponse> getHistory();
}
