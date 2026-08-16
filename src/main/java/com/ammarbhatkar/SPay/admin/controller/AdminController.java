package com.ammarbhatkar.SPay.admin.controller;

import com.ammarbhatkar.SPay.admin.dto.response.DlqEventResponse;
import com.ammarbhatkar.SPay.admin.dto.response.OutboxEventResponse;
import com.ammarbhatkar.SPay.admin.dto.response.ProcessedEventResponse;
import com.ammarbhatkar.SPay.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dlq")
    public ResponseEntity<List<DlqEventResponse>> getDlqEvents() {
        return ResponseEntity.ok(adminService.getDlqEvents());
    }

    @GetMapping("/outbox")
    public ResponseEntity<List<OutboxEventResponse>> getOutboxEvents() {
        return ResponseEntity.ok(adminService.getOutboxEvents());
    }

    @GetMapping("/processed-events")
    public ResponseEntity<List<ProcessedEventResponse>> getProcessedEvents() {
        return ResponseEntity.ok(adminService.getProcessedEvents());
    }
}
