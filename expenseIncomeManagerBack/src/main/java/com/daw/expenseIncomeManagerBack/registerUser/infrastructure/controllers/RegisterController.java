package com.daw.expenseIncomeManagerBack.registerUser.infrastructure.controllers;

import com.daw.expenseIncomeManagerBack.registerUser.application.RegisterRequest;
import com.daw.expenseIncomeManagerBack.registerUser.domain.RegisterUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class RegisterController {

    private final RegisterUseCase registerUseCase;

    public RegisterController(RegisterUseCase registerUseCase) {
        this.registerUseCase = registerUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            registerUseCase.execute(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado con éxito");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}