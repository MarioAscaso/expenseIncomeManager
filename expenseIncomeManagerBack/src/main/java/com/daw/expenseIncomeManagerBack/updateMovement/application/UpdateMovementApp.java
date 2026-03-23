package com.daw.expenseIncomeManagerBack.updateMovement.application;

import com.daw.expenseIncomeManagerBack.shared.domain.Movement;
import com.daw.expenseIncomeManagerBack.shared.domain.MovementRepository;
import com.daw.expenseIncomeManagerBack.shared.domain.MovementTypeEnum;
import com.daw.expenseIncomeManagerBack.shared.domain.User;
import com.daw.expenseIncomeManagerBack.shared.domain.UserRepository;
import com.daw.expenseIncomeManagerBack.updateMovement.domain.UpdateMovementUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateMovementApp implements UpdateMovementUseCase {

    private final MovementRepository movementRepository;
    private final UserRepository userRepository;

    public UpdateMovementApp(MovementRepository movementRepository, UserRepository userRepository) {
        this.movementRepository = movementRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional // Vital para que si falla algo, el saldo no se quede corrompido
    public Movement execute(Long id, UpdateMovementRequest request) {
        Movement movement = movementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));

        User user = movement.getUser();

        // 1. REVERTIMOS EL SALDO ANTIGUO
        if (movement.getType() == MovementTypeEnum.INCOME) {
            user.getAccount().setBalance(user.getAccount().getBalance().subtract(movement.getAmount()));
        } else {
            user.getAccount().setBalance(user.getAccount().getBalance().add(movement.getAmount()));
        }

        // 2. ACTUALIZAMOS LOS DATOS DEL MOVIMIENTO
        movement.setDescription(request.getDescription());
        movement.setAmount(request.getAmount());
        movement.setType(request.getType());
        // Nota: La fecha (createdAt) no se toca, cumpliendo el enunciado.

        // 3. APLICAMOS EL NUEVO SALDO
        if (movement.getType() == MovementTypeEnum.INCOME) {
            user.getAccount().setBalance(user.getAccount().getBalance().add(movement.getAmount()));
        } else {
            user.getAccount().setBalance(user.getAccount().getBalance().subtract(movement.getAmount()));
        }

        userRepository.save(user);
        return movementRepository.save(movement);
    }
}