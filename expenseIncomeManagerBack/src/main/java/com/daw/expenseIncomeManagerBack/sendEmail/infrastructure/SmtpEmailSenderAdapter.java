package com.daw.expenseIncomeManagerBack.sendEmail.infrastructure;

import com.daw.expenseIncomeManagerBack.sendEmail.domain.EmailSenderPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpEmailSenderAdapter implements EmailSenderPort {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void send(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@gestorfinanciero.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            System.out.println("Email enviado correctamente a: " + to);
        } catch (Exception e) {
            System.err.println("Error al enviar el email a " + to + ": " + e.getMessage());
        }
    }
}