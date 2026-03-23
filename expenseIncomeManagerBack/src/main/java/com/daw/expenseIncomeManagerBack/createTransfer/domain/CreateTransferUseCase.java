package com.daw.expenseIncomeManagerBack.createTransfer.domain;

import com.daw.expenseIncomeManagerBack.createTransfer.application.TransferRequest;

public interface CreateTransferUseCase {
    void execute(TransferRequest request);
}