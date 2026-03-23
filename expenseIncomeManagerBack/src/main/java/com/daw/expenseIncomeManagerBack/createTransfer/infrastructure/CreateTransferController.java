package com.daw.expenseIncomeManagerBack.createTransfer.infrastructure;

import com.daw.expenseIncomeManagerBack.createTransfer.application.TransferRequest;
import com.daw.expenseIncomeManagerBack.createTransfer.domain.CreateTransferUseCase;
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