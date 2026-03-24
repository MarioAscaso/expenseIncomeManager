package com.daw.expenseIncomeManagerBack.sendEmail.application;

import com.daw.expenseIncomeManagerBack.sendEmail.domain.EmailSenderPort;
import com.daw.expenseIncomeManagerBack.sendEmail.domain.SendEmailUseCase;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SendEmailApp implements SendEmailUseCase {

    private final EmailSenderPort emailSenderPort;

    public SendEmailApp(EmailSenderPort emailSenderPort) {
        this.emailSenderPort = emailSenderPort;
    }

    @Override
    public void sendMovementNotification(String email, String type, BigDecimal amount, String description) {
        String subject = "Alerta de Movimiento: " + type;
        String body = String.format("Se ha registrado un nuevo movimiento en su cuenta.\n\nTipo: %s\nImporte: %s€\nDescripción: %s", type, amount, description);

        emailSenderPort.send(email, subject, body);
    }
}