package com.daw.expenseIncomeManagerBack.RegisterUser.domain;

import com.daw.expenseIncomeManagerBack.RegisterUser.application.RegisterRequest;

public interface RegisterUseCase {
    void execute(RegisterRequest request);
}