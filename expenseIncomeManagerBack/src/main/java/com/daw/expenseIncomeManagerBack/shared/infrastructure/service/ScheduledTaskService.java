package com.daw.expenseIncomeManagerBack.shared.infrastructure.service;

import com.daw.expenseIncomeManagerBack.sendTransfer.domain.CreateTransferUseCase;
import com.daw.expenseIncomeManagerBack.sendTransfer.application.TransferRequest;
import com.daw.expenseIncomeManagerBack.shared.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduledTaskService {

    private final ScheduledTransferRepository scheduledRepository;
    private final CreateTransferUseCase createTransferUseCase;

    public ScheduledTaskService(ScheduledTransferRepository scheduledRepository, CreateTransferUseCase createTransferUseCase) {
        this.scheduledRepository = scheduledRepository;
        this.createTransferUseCase = createTransferUseCase;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void executePendingTransfers() {
        LocalDate today = LocalDate.now();
        List<ScheduledTransfer> pendingTransfers = scheduledRepository.findByExecutionDateAndExecutedFalse(today);

        for (ScheduledTransfer st : pendingTransfers) {
            try {
                TransferRequest request = new TransferRequest();
                request.setOriginUserId(st.getOriginUser().getId());
                request.setTargetUsername(st.getTargetUser().getUsername());
                request.setAmount(st.getAmount());
                request.setDescription("[PROGRAMADA] " + st.getDescription());

                createTransferUseCase.execute(request);

                st.setExecuted(true);
                scheduledRepository.save(st);

                System.out.println("Transferencia programada " + st.getId() + " ejecutada con éxito.");
            } catch (Exception e) {
                System.err.println("Error crítico al ejecutar transferencia programada " + st.getId() + ": " + e.getMessage());
            }
        }
    }
}