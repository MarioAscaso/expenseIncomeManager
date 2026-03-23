package com.daw.expenseIncomeManagerBack.loginUser.domain;

import com.daw.expenseIncomeManagerBack.loginUser.application.LoginRequest;
import com.daw.expenseIncomeManagerBack.loginUser.application.LoginResponse;

public interface LoginUseCase {
    LoginResponse execute(LoginRequest request);
}