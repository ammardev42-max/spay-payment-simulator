package com.ammarbhatkar.SPay.simulator.controller;

import com.ammarbhatkar.SPay.simulator.dto.request.UpdateSimulatorRuleRequest;
import com.ammarbhatkar.SPay.simulator.dto.response.SimulatorRuleResponse;
import com.ammarbhatkar.SPay.simulator.service.SimulatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/simulator/rules")
@RequiredArgsConstructor
public class SimulatorController {

    private final SimulatorService simulatorService;

    @GetMapping("/active")
    public ResponseEntity<SimulatorRuleResponse> getActiveRule() {
        return ResponseEntity.ok(simulatorService.getActiveRuleResponse());
    }

    @PutMapping
    public ResponseEntity<SimulatorRuleResponse> updateRule(
            @RequestBody @Valid UpdateSimulatorRuleRequest request
    ) {
        return ResponseEntity.ok(simulatorService.updateRule(request));
    }
}
