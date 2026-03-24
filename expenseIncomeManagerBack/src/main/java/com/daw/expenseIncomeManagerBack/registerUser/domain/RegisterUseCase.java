package com.daw.expenseIncomeManagerBack.registerUser.domain;

import com.daw.expenseIncomeManagerBack.registerUser.application.RegisterRequest;

public interface RegisterUseCase {
    void execute(RegisterRequest request);
}