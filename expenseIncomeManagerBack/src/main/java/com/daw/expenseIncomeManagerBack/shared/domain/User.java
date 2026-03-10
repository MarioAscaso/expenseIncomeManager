package com.daw.expenseIncomeManagerBack.shared.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "users") // "user" es palabra reservada en algunas BD, mejor "users"
@Data // Genera getters, setters, toString, etc. (Requiere Lombok)
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleEnum role;

    // Almacenamos el saldo actual para acceso rápido (como pedía el enunciado)
    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;
}
