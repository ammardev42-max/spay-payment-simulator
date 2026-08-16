package com.ammarbhatkar.SPay.payment.controller;


import com.ammarbhatkar.SPay.payment.dto.request.CreateUpiPaymentRequest;
import com.ammarbhatkar.SPay.payment.dto.response.PaymentAttemptResponse;
import com.ammarbhatkar.SPay.payment.dto.response.PaymentResponse;
import com.ammarbhatkar.SPay.payment.dto.response.PaymentTimelineResponse;
import com.ammarbhatkar.SPay.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/upi")
    public ResponseEntity<PaymentResponse>createUpiPayment
            (@RequestBody @Valid CreateUpiPaymentRequest request,
             @RequestHeader("X-Idempotency-Key") String idempotencyKey){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.createUpiPayment(request, idempotencyKey));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentService.getPayment(paymentId));
    }

    @GetMapping("/{paymentId}/timeline")
    public ResponseEntity<List<PaymentTimelineResponse>> getTimeline(
            @PathVariable UUID paymentId
    ) {
        return ResponseEntity.ok(paymentService.getTimeline(paymentId));
    }

    @GetMapping("/{paymentId}/attempts")
    public ResponseEntity<List<PaymentAttemptResponse>> getAttempts(
            @PathVariable UUID paymentId
    ) {
        return ResponseEntity.ok(paymentService.getAttempts(paymentId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<PaymentResponse>> getHistory() {
        return ResponseEntity.ok(paymentService.getHistory());
    }
}
