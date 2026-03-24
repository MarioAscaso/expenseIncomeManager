package com.daw.expenseIncomeManagerBack.createMovement.infrastructure.controllers;

import com.daw.expenseIncomeManagerBack.createMovement.application.CreateMovementRequest;
import com.daw.expenseIncomeManagerBack.createMovement.domain.CreateMovementUseCase;
import com.daw.expenseIncomeManagerBack.shared.domain.Movement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movements")
@CrossOrigin(origins = "*")
public class CreateMovementController {

    private final CreateMovementUseCase createMovementUseCase;

    public CreateMovementController(CreateMovementUseCase createMovementUseCase) {
        this.createMovementUseCase = createMovementUseCase;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Movement> createMovement(@Valid @ModelAttribute CreateMovementRequest request) {
        Movement savedMovement = createMovementUseCase.execute(request);
        return new ResponseEntity<>(savedMovement, HttpStatus.CREATED);
    }
}