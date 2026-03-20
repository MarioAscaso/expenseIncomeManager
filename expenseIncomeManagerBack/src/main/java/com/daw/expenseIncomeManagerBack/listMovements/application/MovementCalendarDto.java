package com.daw.expenseIncomeManagerBack.listMovements.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovementCalendarDto {
    private String id;              // ID del evento (FullCalendar prefiere strings aquí)
    private String title;           // Lo que se verá en el calendario (Ej: "Mercadona (-50€)")
    private String start;           // Fecha en formato ISO (Ej: "2024-05-15T10:30:00")
    private String backgroundColor; // Color de fondo (Verde para ingresos, Rojo para gastos)
    private String borderColor;     // Color del borde

    // Propiedades extra que FullCalendar guardará por si las necesitamos al hacer clic
    private String description;
    private BigDecimal amount;
    private String type;
}