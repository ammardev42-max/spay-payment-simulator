package com.ammarbhatkar.SPay.payment.repository;

import com.ammarbhatkar.SPay.payment.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {

    List<PaymentTransaction> findBySenderUser_IdOrReceiverUser_IdOrderByCreatedAtDesc(
            UUID senderUserId,
            UUID receiverUserId
    );
}