package com.daw.expenseIncomeManagerBack.sendBizum.domain;

import com.daw.expenseIncomeManagerBack.sendBizum.application.BizumRequest;

public interface SendBizumUseCase {
    void execute(BizumRequest request);
}