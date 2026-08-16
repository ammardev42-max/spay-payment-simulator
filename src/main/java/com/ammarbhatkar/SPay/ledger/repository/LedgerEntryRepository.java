package com.ammarbhatkar.SPay.ledger.repository;

import com.ammarbhatkar.SPay.ledger.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {

    List<LedgerEntry> findByTransaction_Id(UUID transactionId);
}