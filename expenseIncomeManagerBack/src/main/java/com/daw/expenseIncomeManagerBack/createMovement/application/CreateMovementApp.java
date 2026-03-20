package com.daw.expenseIncomeManagerBack.createMovement.application;

import com.daw.expenseIncomeManagerBack.createMovement.domain.CreateMovementUseCase;
import com.daw.expenseIncomeManagerBack.shared.domain.Movement;
import com.daw.expenseIncomeManagerBack.shared.domain.MovementRepository;
import com.daw.expenseIncomeManagerBack.shared.domain.User;
import com.daw.expenseIncomeManagerBack.shared.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateMovementApp implements CreateMovementUseCase {

    private final MovementRepository movementRepository;
    private final UserRepository userRepository;

    public CreateMovementApp(MovementRepository movementRepository, UserRepository userRepository) {
        this.movementRepository = movementRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Movement execute(CreateMovementRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Movement movement = new Movement();
        movement.setDescription(request.getDescription());
        movement.setAmount(request.getAmount());
        movement.setType(request.getType());
        movement.setUser(user);

        if (request.getType().name().equals("INCOME")) {
            user.setBalance(user.getBalance().add(request.getAmount()));
        } else {
            user.setBalance(user.getBalance().subtract(request.getAmount()));
        }
        userRepository.save(user);

        return movementRepository.save(movement);
    }
}