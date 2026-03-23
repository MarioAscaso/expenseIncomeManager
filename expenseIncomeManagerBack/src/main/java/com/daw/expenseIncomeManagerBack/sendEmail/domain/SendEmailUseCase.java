package com.daw.expenseIncomeManagerBack.sendEmail.domain;

public interface SendEmailUseCase {
    void sendMovementNotification(String email, String type, java.math.BigDecimal amount, String description);
}