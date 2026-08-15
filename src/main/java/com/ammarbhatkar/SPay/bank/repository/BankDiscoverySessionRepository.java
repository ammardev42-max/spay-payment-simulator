package com.ammarbhatkar.SPay.bank.repository;

import com.ammarbhatkar.SPay.bank.entity.BankDiscoverySession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BankDiscoverySessionRepository extends JpaRepository<BankDiscoverySession, UUID> {
}