package com.ammarbhatkar.SPay.simulator.service;

import com.ammarbhatkar.SPay.common.enums.PaymentAttemptOutcome;
import com.ammarbhatkar.SPay.simulator.dto.request.UpdateSimulatorRuleRequest;
import com.ammarbhatkar.SPay.simulator.dto.response.SimulatorRuleResponse;
import com.ammarbhatkar.SPay.simulator.entity.SimulatorRule;

public interface SimulatorService {

    SimulatorRule getActiveRule();

    PaymentAttemptOutcome decideOutcome(SimulatorRule simulatorRule);

    SimulatorRuleResponse getActiveRuleResponse();

    SimulatorRuleResponse updateRule(UpdateSimulatorRuleRequest request);
}
