package com.ammarbhatkar.SPay.bank.repository;

import com.ammarbhatkar.SPay.bank.entity.BankAccount;
import com.ammarbhatkar.SPay.bank.entity.BankDiscoverySession;
import com.ammarbhatkar.SPay.user.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {

    Optional<BankAccount> findByDiscoverySession(BankDiscoverySession discoverySession);

    List<BankAccount> findByUser(AppUser user);
}