package com.daw.expenseIncomeManagerBack.getUserBalance.domain;

import com.daw.expenseIncomeManagerBack.getUserBalance.application.UserBalanceDto;

public interface GetUserBalanceUseCase {
    UserBalanceDto execute(Long userId);
}