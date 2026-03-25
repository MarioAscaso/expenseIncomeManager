package com.daw.expenseIncomeManagerBack.createMovement.application;

import com.daw.expenseIncomeManagerBack.shared.domain.MovementTypeEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

@Data
public class CreateMovementRequest {

    @NotBlank(message = "La descripción no puede estar vacía")
    private String description;

    @NotNull(message = "El importe es obligatorio")
    @Min(value = 0, message = "El importe no puede ser negativo")
    private BigDecimal amount;

    @NotNull(message = "El tipo de movimiento es obligatorio (INCOME o EXPENSE)")
    private MovementTypeEnum type;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    private MultipartFile file;

    private String date;
}