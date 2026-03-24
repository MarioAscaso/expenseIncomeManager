package com.daw.expenseIncomeManagerBack.registerUser.application;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "El usuario no puede estar vacío")
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;

    @NotBlank(message = "El email no puede estar vacío")
    private String email;

    @NotBlank(message = "El teléfono no puede estar vacío")
    private String phoneNumber;
}