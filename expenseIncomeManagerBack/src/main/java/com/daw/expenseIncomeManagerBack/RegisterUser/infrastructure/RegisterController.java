package com.daw.expenseIncomeManagerBack.RegisterUser.infrastructure;

import com.daw.expenseIncomeManagerBack.RegisterUser.application.RegisterRequest;
import com.daw.expenseIncomeManagerBack.RegisterUser.domain.RegisterUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class RegisterController {

    private final RegisterUseCase registerUseCase; // NUEVO

    public RegisterController(RegisterUseCase registerUseCase) {
        this.registerUseCase = registerUseCase; // NUEVO
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