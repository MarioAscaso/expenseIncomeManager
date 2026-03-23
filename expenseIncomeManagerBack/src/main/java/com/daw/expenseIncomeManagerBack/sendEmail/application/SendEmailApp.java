package com.daw.expenseIncomeManagerBack.sendEmail.application;

import com.daw.expenseIncomeManagerBack.sendEmail.domain.SendEmailUseCase;
import com.daw.expenseIncomeManagerBack.shared.infrastructure.service.EmailService;
import org.springframework.stereotype.Service;

@Service
public class SendEmailApp implements SendEmailUseCase {

    private final EmailService emailService;

    public SendEmailApp(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void sendMovementNotification(String email, String type, java.math.BigDecimal amount, String description) {
        String subject = "Alerta de Movimiento: " + type;
        String body = String.format(
                "Se ha registrado un nuevo movimiento en su cuenta.\n\nTipo: %s\nImporte: %s€\nDescripción: %s",
                type, amount, description
        );
        emailService.sendEmail(email, subject, body);
    }
}