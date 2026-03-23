package com.daw.expenseIncomeManagerBack.deleteMovement.domain;

public interface DeleteMovementUseCase {
    void execute(Long movementId);
}