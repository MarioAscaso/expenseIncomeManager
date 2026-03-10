package com.daw.expenseIncomeManagerBack.shared.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal amount; // Guardaremos el valor en positivo siempre

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementTypeEnum type; // INCOME o EXPENSE

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Relación: Muchos movimientos pertenecen a un Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Método que se ejecuta justo antes de guardar en base de datos
    // Así cumplimos el requisito de: "Esta fecha será recogida por el sistema"
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}