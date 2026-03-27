package com.daw.expenseIncomeManagerBack.sendTransfer.application;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ScheduledTransferRequest {
    private Long originUserId;
    private String targetUsername;
    private BigDecimal amount;
    private String description;
    private LocalDate executionDate;
}