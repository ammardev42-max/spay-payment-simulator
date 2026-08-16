package com.ammarbhatkar.SPay.simulator.service.impl;

import com.ammarbhatkar.SPay.common.enums.PaymentAttemptOutcome;
import com.ammarbhatkar.SPay.common.enums.SimulatorMode;
import com.ammarbhatkar.SPay.common.security.UserContext;
import com.ammarbhatkar.SPay.simulator.dto.request.UpdateSimulatorRuleRequest;
import com.ammarbhatkar.SPay.simulator.dto.response.SimulatorRuleResponse;
import com.ammarbhatkar.SPay.simulator.entity.SimulatorRule;
import com.ammarbhatkar.SPay.simulator.repository.SimulatorRuleRepository;
import com.ammarbhatkar.SPay.simulator.service.SimulatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SimulatorServiceImpl implements SimulatorService {

    private final SimulatorRuleRepository simulatorRuleRepository;
    private final UserContext userContext;

    @Override
    public SimulatorRule getActiveRule() {
        return simulatorRuleRepository
                .findFirstByActiveTrueOrderByUpdatedAtDesc()
                .orElseGet(this::createDefaultRule);
    }

    @Override
    public PaymentAttemptOutcome decideOutcome(SimulatorRule simulatorRule) {
        return switch (simulatorRule.getMode()) {
            case ALWAYS_SUCCESS -> PaymentAttemptOutcome.SUCCESS;
            case ALWAYS_RETRYABLE_FAILURE -> PaymentAttemptOutcome.RETRYABLE_FAILURE;
            case ALWAYS_NON_RETRYABLE_FAILURE -> PaymentAttemptOutcome.NON_RETRYABLE_FAILURE;
        };
    }

    @Override
    public SimulatorRuleResponse getActiveRuleResponse() {
        return toResponse(getActiveRule());
    }

    @Override
    @Transactional
    public SimulatorRuleResponse updateRule(UpdateSimulatorRuleRequest request) {
        simulatorRuleRepository.findFirstByActiveTrueOrderByUpdatedAtDesc()
                .ifPresent(existingRule -> {
                    existingRule.setActive(false);
                    simulatorRuleRepository.save(existingRule);
                });

        SimulatorRule simulatorRule = SimulatorRule.builder()
                .mode(request.mode())
                .successRate(100)
                .pendingRate(0)
                .timeoutEnabled(false)
                .maxAttempts(request.maxAttempts() == null ? 3 : request.maxAttempts())
                .active(true)
                .updatedBy(userContext.getEmail())
                .build();

        return toResponse(simulatorRuleRepository.save(simulatorRule));
    }

    @Transactional
    protected SimulatorRule createDefaultRule() {
        SimulatorRule simulatorRule = SimulatorRule.builder()
                .mode(SimulatorMode.ALWAYS_SUCCESS)
                .successRate(100)
                .pendingRate(0)
                .timeoutEnabled(false)
                .maxAttempts(3)
                .active(true)
                .updatedBy("system")
                .build();

        return simulatorRuleRepository.save(simulatorRule);
    }

    private SimulatorRuleResponse toResponse(SimulatorRule simulatorRule) {
        return new SimulatorRuleResponse(
                simulatorRule.getId(),
                simulatorRule.getMode().name(),
                simulatorRule.getSuccessRate(),
                simulatorRule.getPendingRate(),
                simulatorRule.getTimeoutEnabled(),
                simulatorRule.getMaxAttempts(),
                simulatorRule.getActive(),
                simulatorRule.getUpdatedBy(),
                simulatorRule.getUpdatedAt()
        );
    }
}
