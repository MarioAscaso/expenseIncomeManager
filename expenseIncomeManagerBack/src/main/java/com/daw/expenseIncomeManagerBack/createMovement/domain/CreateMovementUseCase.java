package com.daw.expenseIncomeManagerBack.createMovement.domain;

import com.daw.expenseIncomeManagerBack.createMovement.application.CreateMovementRequest;
import com.daw.expenseIncomeManagerBack.shared.domain.Movement;

public interface CreateMovementUseCase {
    Movement execute(CreateMovementRequest request);
}