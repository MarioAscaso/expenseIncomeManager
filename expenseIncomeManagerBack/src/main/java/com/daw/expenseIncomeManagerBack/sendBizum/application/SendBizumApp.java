package com.daw.expenseIncomeManagerBack.sendBizum.application;

import com.daw.expenseIncomeManagerBack.sendBizum.domain.SendBizumUseCase;
import com.daw.expenseIncomeManagerBack.sendEmail.domain.SendEmailUseCase;
import com.daw.expenseIncomeManagerBack.shared.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SendBizumApp implements SendBizumUseCase {

    private final UserRepository userRepository;
    private final MovementRepository movementRepository;
    private final SendEmailUseCase sendEmailUseCase;

    public SendBizumApp(UserRepository userRepository, MovementRepository movementRepository, SendEmailUseCase sendEmailUseCase) {
        this.userRepository = userRepository;
        this.movementRepository = movementRepository;
        this.sendEmailUseCase = sendEmailUseCase;
    }

    @Override
    @Transactional
    public void execute(BizumRequest request) {
        User origin = userRepository.findById(request.getOriginUserId()).orElseThrow(() -> new RuntimeException("Usuario origen no encontrado"));

        User target = userRepository.findByPhoneNumber(request.getTargetPhoneNumber()).orElseThrow(() -> new RuntimeException("No existe ningún usuario asociado a ese número de teléfono"));

        if (origin.getId().equals(target.getId())) {
            throw new RuntimeException("No puedes hacerte un Bizum a ti mismo");
        }

        if (origin.getAccount().getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Saldo insuficiente para realizar el Bizum");
        }

        origin.getAccount().setBalance(origin.getAccount().getBalance().subtract(request.getAmount()));
        target.getAccount().setBalance(target.getAccount().getBalance().add(request.getAmount()));

        Movement expense = new Movement();
        expense.setDescription("Bizum enviado a " + target.getUsername() + ": " + request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setType(MovementTypeEnum.EXPENSE);
        expense.setUser(origin);

        Movement income = new Movement();
        income.setDescription("Bizum recibido de " + origin.getUsername() + ": " + request.getDescription());
        income.setAmount(request.getAmount());
        income.setType(MovementTypeEnum.INCOME);
        income.setUser(target);

        userRepository.save(origin);
        userRepository.save(target);
        movementRepository.save(expense);
        movementRepository.save(income);

        if (origin.getEmail() != null && origin.getNotificationsEnabled()) {
            sendEmailUseCase.sendMovementNotification(origin.getEmail(), "BIZUM ENVIADO", request.getAmount(), "A " + target.getUsername() + " (" + request.getTargetPhoneNumber() + ")");
        }
        if (target.getEmail() != null && target.getNotificationsEnabled()) {
            sendEmailUseCase.sendMovementNotification(target.getEmail(), "BIZUM RECIBIDO", request.getAmount(), "De " + origin.getUsername());
        }
    }
}