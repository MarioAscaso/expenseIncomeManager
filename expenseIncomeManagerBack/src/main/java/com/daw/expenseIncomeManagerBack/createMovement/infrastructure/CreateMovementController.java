package com.daw.expenseIncomeManagerBack.createMovement.infrastructure;

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

    // NUEVO: Le decimos que consume multipart/form-data y usamos @ModelAttribute
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Movement> createMovement(@Valid @ModelAttribute CreateMovementRequest request) {
        Movement savedMovement = createMovementUseCase.execute(request);
        return new ResponseEntity<>(savedMovement, HttpStatus.CREATED);
    }
}