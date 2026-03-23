package com.daw.expenseIncomeManagerBack.getUserBalance.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBalanceDto {
    private BigDecimal balance;
}