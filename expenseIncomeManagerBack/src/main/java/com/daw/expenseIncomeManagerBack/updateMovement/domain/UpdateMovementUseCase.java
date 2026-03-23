package com.daw.expenseIncomeManagerBack.updateMovement.domain;

import com.daw.expenseIncomeManagerBack.shared.domain.Movement;
import com.daw.expenseIncomeManagerBack.updateMovement.application.UpdateMovementRequest;

public interface UpdateMovementUseCase {
    Movement execute(Long id, UpdateMovementRequest request);
}