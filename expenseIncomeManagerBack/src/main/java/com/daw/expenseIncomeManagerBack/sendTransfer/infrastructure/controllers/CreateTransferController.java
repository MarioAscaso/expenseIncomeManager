package com.daw.expenseIncomeManagerBack.sendTransfer.infrastructure.controllers;

import com.daw.expenseIncomeManagerBack.sendTransfer.application.TransferRequest;
import com.daw.expenseIncomeManagerBack.sendTransfer.domain.CreateTransferUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@CrossOrigin(origins = "*")
public class CreateTransferController {

    private final CreateTransferUseCase createTransferUseCase;

    public CreateTransferController(CreateTransferUseCase createTransferUseCase) {
        this.createTransferUseCase = createTransferUseCase;
    }

    @PostMapping
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {
        try {
            createTransferUseCase.execute(request);
            return ResponseEntity.ok().body("Transferencia realizada con éxito");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}