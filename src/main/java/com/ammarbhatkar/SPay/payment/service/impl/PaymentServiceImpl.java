package com.ammarbhatkar.SPay.payment.service.impl;

import com.ammarbhatkar.SPay.bank.entity.BankAccount;
import com.ammarbhatkar.SPay.bank.entity.UpiCredential;
import com.ammarbhatkar.SPay.bank.repository.BankAccountRepository;
import com.ammarbhatkar.SPay.bank.repository.UpiCredentialRepository;
import com.ammarbhatkar.SPay.common.enums.*;
import com.ammarbhatkar.SPay.common.exception.BusinessRuleViolationException;
import com.ammarbhatkar.SPay.common.exception.ResourceNotFoundException;
import com.ammarbhatkar.SPay.common.security.UserContext;
import com.ammarbhatkar.SPay.ledger.entity.LedgerEntry;
import com.ammarbhatkar.SPay.ledger.repository.LedgerEntryRepository;
import com.ammarbhatkar.SPay.payment.dto.request.CreateUpiPaymentRequest;
import com.ammarbhatkar.SPay.payment.dto.response.PaymentResponse;
import com.ammarbhatkar.SPay.payment.dto.response.PaymentTimelineResponse;
import com.ammarbhatkar.SPay.payment.entity.PaymentTimelineEvent;
import com.ammarbhatkar.SPay.payment.entity.PaymentTransaction;
import com.ammarbhatkar.SPay.payment.mapper.PaymentMapper;
import com.ammarbhatkar.SPay.payment.mapper.PaymentTimelineMapper;
import com.ammarbhatkar.SPay.payment.repository.PaymentTimelineEventRepository;
import com.ammarbhatkar.SPay.payment.repository.PaymentTransactionRepository;
import com.ammarbhatkar.SPay.payment.service.PaymentService;
import com.ammarbhatkar.SPay.upi.entity.UpiHandle;
import com.ammarbhatkar.SPay.upi.repository.UpiHandleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentMapper paymentMapper;
    private  final PaymentTransactionRepository paymentTransactionRepository;
    private final UserContext userContext;
    private final PaymentTimelineEventRepository paymentTimelineEventRepository;
    private final PaymentTimelineMapper paymentTimelineMapper;
    private  final UpiHandleRepository upiHandleRepository;
    private static final String INR = "INR";
    private final UpiCredentialRepository upiCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final BankAccountRepository bankAccountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;



    @Override
    @Transactional
    public PaymentResponse createUpiPayment(CreateUpiPaymentRequest request) {
        String senderUpi = request.senderUpi().trim().toLowerCase(Locale.ROOT);
        String receiverUpi = request.receiverUpi().trim().toLowerCase(Locale.ROOT);


        UpiHandle senderHandle = upiHandleRepository
                .findByUpiIdAndStatus(senderUpi, UpiHandleStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("UpiHandle", senderUpi));

        if (!senderHandle.getUser().getId().equals(userContext.getUserId())) {
            throw new BusinessRuleViolationException(
                    "SENDER_UPI_ACCESS_DENIED",
                    "You cannot pay from this UPI ID"
            );
        }
        UpiHandle receiverHandle = upiHandleRepository
                .findByUpiIdAndStatus(receiverUpi, UpiHandleStatus.ACTIVE)
                .orElseThrow(()
                        -> new ResourceNotFoundException("UpiHandle", receiverUpi)
                );

        BankAccount senderAccount = senderHandle.getBankAccount();
        BankAccount receiverAccount = receiverHandle.getBankAccount();

        PaymentTransaction transaction = PaymentTransaction.builder()
                .type(PaymentType.UPI)
                .status(PaymentStatus.INITIATED)
                .senderUser(senderHandle.getUser())
                .senderBankAccount(senderHandle.getBankAccount())
                .receiverUser(receiverHandle.getUser())
                .receiverBankAccount(receiverHandle.getBankAccount())
                .senderUpi(senderHandle.getUpiId())
                .receiverUpi(receiverHandle.getUpiId())
                .amountPaise(request.amountPaise())
                .currency(INR)
                .note(request.note())
                .currentAttempt(1)
                .build();
        PaymentTransaction savedTransaction= paymentTransactionRepository.save(transaction);
        addTimeline(savedTransaction, PaymentStatus.INITIATED, "Payment request created");
        savedTransaction.setStatus(PaymentStatus.VALIDATING);
        addTimeline(savedTransaction, PaymentStatus.VALIDATING, "Validating sender, receiver, balance and UPI PIN");

        validatePayment(request, senderAccount, receiverAccount);

        savedTransaction.setStatus(PaymentStatus.PROCESSING);
        addTimeline(savedTransaction, PaymentStatus.PROCESSING, "Moving money between bank accounts");
        senderAccount.setBalancePaise(senderAccount.getBalancePaise() - request.amountPaise());
        receiverAccount.setBalancePaise(receiverAccount.getBalancePaise() + request.amountPaise());


        bankAccountRepository.save(senderAccount);
        bankAccountRepository.save(receiverAccount);
        createLedgerEntries(savedTransaction, senderAccount, receiverAccount, request.amountPaise());
        savedTransaction.setStatus(PaymentStatus.SUCCESS);
        savedTransaction.setCompletedAt(Instant.now());
        PaymentTransaction completedTransaction = paymentTransactionRepository.save(savedTransaction);
        addTimeline(completedTransaction, PaymentStatus.SUCCESS, "Payment completed successfully");

        return paymentMapper.toResponse(completedTransaction);
    }

    @Override
    public PaymentResponse getPayment(UUID paymentId) {
        PaymentTransaction transaction = paymentTransactionRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentTransaction", paymentId.toString()));

        if (!transaction.getSenderUser().getId().equals(userContext.getUserId())
                && !transaction.getReceiverUser().getId().equals(userContext.getUserId())) {
            throw new BusinessRuleViolationException(
                    "PAYMENT_ACCESS_DENIED",
                    "You cannot access this payment"
            );
        }
        return paymentMapper.toResponse(transaction);
    }

    @Override
    public List<PaymentTimelineResponse> getTimeline(UUID paymentId) {
        getPayment(paymentId);

        return paymentTimelineMapper.toResponseList(
                paymentTimelineEventRepository
                        .findByTransaction_IdOrderByCreatedAtAsc(paymentId)
        );
    }

    @Override
    public List<PaymentResponse> getHistory() {
        return paymentMapper.toResponseList(
                paymentTransactionRepository
                        .findBySenderUser_IdOrReceiverUser_IdOrderByCreatedAtDesc(
                userContext.getUserId(),
                userContext.getUserId()
        ));
    }

    private void addTimeline(PaymentTransaction transaction, PaymentStatus status, String message) {
        PaymentTimelineEvent event = PaymentTimelineEvent.builder()
                .transaction(transaction)
                .status(status)
                .message(message)
                .build();

        paymentTimelineEventRepository.save(event);
    }

    private void validatePayment(
            CreateUpiPaymentRequest request,
            BankAccount senderAccount,
            BankAccount receiverAccount
    ) {
        if (senderAccount.getId().equals(receiverAccount.getId())) {
            throw new BusinessRuleViolationException(
                    "SENDER_RECEIVER_SAME",
                    "Sender and receiver bank account cannot be same"
            );
        }

        if (senderAccount.getStatus() != BankAccountStatus.VERIFIED) {
            throw new BusinessRuleViolationException(
                    "SENDER_BANK_ACCOUNT_NOT_VERIFIED",
                    "Sender bank account is not verified"
            );
        }

        if (receiverAccount.getStatus() != BankAccountStatus.VERIFIED) {
            throw new BusinessRuleViolationException(
                    "RECEIVER_BANK_ACCOUNT_NOT_VERIFIED",
                    "Receiver bank account is not verified"
            );
        }

        UpiCredential credential = upiCredentialRepository.findByBankAccount_Id(senderAccount.getId())
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "UPI_PIN_NOT_SET",
                        "Sender UPI PIN is not set"
                ));

        if (!passwordEncoder.matches(request.upiPin(), credential.getUpiPinHash())) {
            throw new BusinessRuleViolationException(
                    "UPI_PIN_INVALID",
                    "Invalid UPI PIN"
            );
        }

        if (senderAccount.getBalancePaise() < request.amountPaise()) {
            throw new BusinessRuleViolationException(
                    "INSUFFICIENT_BALANCE",
                    "Insufficient balance to complete this payment"
            );
        }
    }

    private void createLedgerEntries(
            PaymentTransaction transaction,
            BankAccount senderAccount,
            BankAccount receiverAccount,
            Long amountPaise
    ) {
        LedgerEntry debitEntry = LedgerEntry.builder()
                .bankAccount(senderAccount)
                .transaction(transaction)
                .direction(LedgerDirection.DEBIT)
                .entryKind(LedgerEntryKind.PAYMENT)
                .amountPaise(amountPaise)
                .balanceAfterPaise(senderAccount.getBalancePaise())
                .deduplicationKey(transaction.getId() + ":DEBIT")
                .build();

        LedgerEntry creditEntry = LedgerEntry.builder()
                .bankAccount(receiverAccount)
                .transaction(transaction)
                .direction(LedgerDirection.CREDIT)
                .entryKind(LedgerEntryKind.PAYMENT)
                .amountPaise(amountPaise)
                .balanceAfterPaise(receiverAccount.getBalancePaise())
                .deduplicationKey(transaction.getId() + ":CREDIT")
                .build();

        ledgerEntryRepository.save(debitEntry);
        ledgerEntryRepository.save(creditEntry);
    }
}
