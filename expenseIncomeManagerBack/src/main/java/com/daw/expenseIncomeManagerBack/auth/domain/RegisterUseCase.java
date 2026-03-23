package com.daw.expenseIncomeManagerBack.auth.domain;

import com.daw.expenseIncomeManagerBack.auth.application.RegisterRequest;

public interface RegisterUseCase {
    void execute(RegisterRequest request);
}