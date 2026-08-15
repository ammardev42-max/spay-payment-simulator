package com.ammarbhatkar.SPay.bank.repository;

import com.ammarbhatkar.SPay.bank.entity.UpiCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UpiCredentialRepository extends JpaRepository<UpiCredential, UUID> {

    Optional<UpiCredential> findByBankAccount_Id(UUID bankAccountId);

    boolean existsByBankAccount_Id(UUID bankAccountId);
}
