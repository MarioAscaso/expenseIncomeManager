package com.daw.expenseIncomeManagerBack.sendEmail.domain;

public interface EmailSenderPort {
    void send(String to, String subject, String body);
}