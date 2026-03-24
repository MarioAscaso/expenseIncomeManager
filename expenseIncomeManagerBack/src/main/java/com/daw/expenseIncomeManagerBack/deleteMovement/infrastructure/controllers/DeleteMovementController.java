package com.daw.expenseIncomeManagerBack.deleteMovement.infrastructure.controllers;

import com.daw.expenseIncomeManagerBack.deleteMovement.domain.DeleteMovementUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movements")
@CrossOrigin(origins = "*")
public class DeleteMovementController {

    private final DeleteMovementUseCase deleteMovementUseCase;

    public DeleteMovementController(DeleteMovementUseCase deleteMovementUseCase) {
        this.deleteMovementUseCase = deleteMovementUseCase;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovement(@PathVariable Long id) {
        deleteMovementUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}