package com.daw.expenseIncomeManagerBack.listMovements.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovementCalendarDto {
    private String id;
    private String title;
    private String start;
    private String backgroundColor;
    private String borderColor;

    private String description;
    private BigDecimal amount;
    private String type;
    private String attachedFileUrl;
}