package com.daw.expenseIncomeManagerBack.auth.infrastructure;

import com.daw.expenseIncomeManagerBack.auth.application.LoginRequest;
import com.daw.expenseIncomeManagerBack.auth.application.LoginResponse;
import com.daw.expenseIncomeManagerBack.auth.application.RegisterRequest;
import com.daw.expenseIncomeManagerBack.auth.domain.LoginUseCase;
import com.daw.expenseIncomeManagerBack.auth.domain.RegisterUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase; // NUEVO

    public AuthController(LoginUseCase loginUseCase, RegisterUseCase registerUseCase) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase; // NUEVO
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = loginUseCase.execute(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // NUEVO ENDPOINT DE REGISTRO
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