package com.daw.expenseIncomeManagerBack.auth.domain;

import com.daw.expenseIncomeManagerBack.auth.application.LoginRequest;
import com.daw.expenseIncomeManagerBack.auth.application.LoginResponse;

public interface LoginUseCase {
    LoginResponse execute(LoginRequest request);
}