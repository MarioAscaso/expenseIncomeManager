package com.daw.expenseIncomeManagerBack.sendTransfer.domain;

import com.daw.expenseIncomeManagerBack.sendTransfer.application.TransferRequest;

public interface CreateTransferUseCase {
    void execute(TransferRequest request);
}