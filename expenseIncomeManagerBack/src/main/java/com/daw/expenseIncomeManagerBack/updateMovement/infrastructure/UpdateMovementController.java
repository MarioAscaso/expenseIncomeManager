package com.daw.expenseIncomeManagerBack.updateMovement.infrastructure;

import com.daw.expenseIncomeManagerBack.shared.domain.Movement;
import com.daw.expenseIncomeManagerBack.updateMovement.application.UpdateMovementRequest;
import com.daw.expenseIncomeManagerBack.updateMovement.domain.UpdateMovementUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movements")
@CrossOrigin(origins = "*")
public class UpdateMovementController {

    private final UpdateMovementUseCase updateMovementUseCase;

    public UpdateMovementController(UpdateMovementUseCase updateMovementUseCase) {
        this.updateMovementUseCase = updateMovementUseCase;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Movement> updateMovement(@PathVariable Long id, @Valid @RequestBody UpdateMovementRequest request) {
        Movement updatedMovement = updateMovementUseCase.execute(id, request);
        return ResponseEntity.ok(updatedMovement);
    }
}