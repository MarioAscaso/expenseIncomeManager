package com.daw.expenseIncomeManagerBack.deleteMovement.application;

import com.daw.expenseIncomeManagerBack.deleteMovement.domain.DeleteMovementUseCase;
import com.daw.expenseIncomeManagerBack.shared.domain.Movement;
import com.daw.expenseIncomeManagerBack.shared.domain.MovementRepository;
import com.daw.expenseIncomeManagerBack.shared.domain.User;
import com.daw.expenseIncomeManagerBack.shared.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteMovementApp implements DeleteMovementUseCase {

    private final MovementRepository movementRepository;
    private final UserRepository userRepository;

    public DeleteMovementApp(MovementRepository movementRepository, UserRepository userRepository) {
        this.movementRepository = movementRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional // Importante: Si falla el borrado, no se guarda el cambio de saldo
    public void execute(Long movementId) {
        // 1. Buscamos el movimiento
        Movement movement = movementRepository.findById(movementId)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));

        // 2. Obtenemos al usuario dueño del movimiento
        User user = movement.getUser();

        // 3. Revertimos el saldo en la cuenta del usuario
        if (movement.getType().name().equals("INCOME")) {
            user.getAccount().setBalance(user.getAccount().getBalance().subtract(movement.getAmount())); // Si era ingreso, restamos
        } else {
            user.getAccount().setBalance(user.getAccount().getBalance().add(movement.getAmount())); // Si era gasto, sumamos
        }
        userRepository.save(user);

        // 4. Borramos el movimiento definitivamente
        movementRepository.delete(movement);
    }
}