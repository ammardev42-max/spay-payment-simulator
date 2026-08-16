package com.ammarbhatkar.SPay.simulator.repository;

import com.ammarbhatkar.SPay.simulator.entity.SimulatorRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SimulatorRuleRepository extends JpaRepository<SimulatorRule, UUID> {

    Optional<SimulatorRule> findFirstByActiveTrueOrderByUpdatedAtDesc();
}
