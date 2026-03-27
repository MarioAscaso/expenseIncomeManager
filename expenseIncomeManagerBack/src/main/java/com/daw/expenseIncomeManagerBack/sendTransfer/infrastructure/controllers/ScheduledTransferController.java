package com.daw.expenseIncomeManagerBack.sendTransfer.infrastructure.controllers;

import com.daw.expenseIncomeManagerBack.sendTransfer.application.ScheduleTransferApp;
import com.daw.expenseIncomeManagerBack.sendTransfer.application.ScheduledTransferRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/scheduled-transfers")
@CrossOrigin(origins = "*")
public class ScheduledTransferController {

    private final ScheduleTransferApp scheduleTransferApp;

    public ScheduledTransferController(ScheduleTransferApp scheduleTransferApp) {
        this.scheduleTransferApp = scheduleTransferApp;
    }

    @PostMapping
    public ResponseEntity<?> schedule(@RequestBody ScheduledTransferRequest request) {
        try {
            scheduleTransferApp.execute(request);
            return ResponseEntity.ok(Map.of("message", "Transferencia programada con éxito"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}