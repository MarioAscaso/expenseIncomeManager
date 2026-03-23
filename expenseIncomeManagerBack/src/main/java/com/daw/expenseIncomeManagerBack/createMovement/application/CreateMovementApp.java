package com.daw.expenseIncomeManagerBack.createMovement.application;

import com.daw.expenseIncomeManagerBack.createMovement.domain.CreateMovementUseCase;
import com.daw.expenseIncomeManagerBack.sendEmail.domain.SendEmailUseCase; // <-- IMPORTANTE
import com.daw.expenseIncomeManagerBack.shared.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateMovementApp implements CreateMovementUseCase {

    private final MovementRepository movementRepository;
    private final UserRepository userRepository;
    private final SendEmailUseCase sendEmailUseCase; // <-- NUEVO

    public CreateMovementApp(MovementRepository movementRepository, UserRepository userRepository, SendEmailUseCase sendEmailUseCase) {
        this.movementRepository = movementRepository;
        this.userRepository = userRepository;
        this.sendEmailUseCase = sendEmailUseCase; // <-- NUEVO
    }

    @Override
    @Transactional
    public Movement execute(CreateMovementRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Movement movement = new Movement();
        movement.setDescription(request.getDescription());
        movement.setAmount(request.getAmount());
        movement.setType(request.getType());
        movement.setUser(user);

        // ... (Tu lógica de archivo se queda IGUAL) ...

        if (request.getType().name().equals("INCOME")) {
            user.getAccount().setBalance(user.getAccount().getBalance().add(request.getAmount()));
        } else {
            user.getAccount().setBalance(user.getAccount().getBalance().subtract(request.getAmount()));
        }
        userRepository.save(user);
        Movement savedMovement = movementRepository.save(movement);

        // --- NUEVO: DISPARAMOS EL EMAIL ---
        if (user.getEmail() != null) {
            sendEmailUseCase.sendMovementNotification(
                    user.getEmail(),
                    savedMovement.getType().name(),
                    savedMovement.getAmount(),
                    savedMovement.getDescription()
            );
        }

        return savedMovement;
    }
}