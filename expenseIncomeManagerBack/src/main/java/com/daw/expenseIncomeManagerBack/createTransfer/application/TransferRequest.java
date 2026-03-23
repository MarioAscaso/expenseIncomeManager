package com.daw.expenseIncomeManagerBack.createTransfer.application;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    private Long originUserId;
    private String targetUsername;
    private BigDecimal amount;
    private String description;
}