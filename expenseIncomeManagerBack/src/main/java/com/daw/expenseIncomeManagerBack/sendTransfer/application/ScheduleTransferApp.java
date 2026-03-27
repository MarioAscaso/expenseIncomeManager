package com.daw.expenseIncomeManagerBack.sendTransfer.application;

import com.daw.expenseIncomeManagerBack.shared.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduleTransferApp {

    private final UserRepository userRepository;
    private final ScheduledTransferRepository scheduledRepository;

    public ScheduleTransferApp(UserRepository userRepository, ScheduledTransferRepository scheduledRepository) {
        this.userRepository = userRepository;
        this.scheduledRepository = scheduledRepository;
    }

    @Transactional
    public void execute(ScheduledTransferRequest request) {
        User origin = userRepository.findById(request.getOriginUserId()).orElseThrow(() -> new RuntimeException("Usuario origen no encontrado"));

        User target = userRepository.findByUsername(request.getTargetUsername()).orElseThrow(() -> new RuntimeException("El usuario destino no existe"));

        if (origin.getAccount().getBalanceForecast().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Saldo previsto insuficiente para programar esta operación");
        }

        ScheduledTransfer st = new ScheduledTransfer();
        st.setOriginUser(origin);
        st.setTargetUser(target);
        st.setAmount(request.getAmount());
        st.setDescription(request.getDescription());
        st.setExecutionDate(request.getExecutionDate());
        st.setExecuted(false);

        origin.getAccount().setBalanceForecast(origin.getAccount().getBalanceForecast().subtract(request.getAmount()));

        target.getAccount().setBalanceForecast(target.getAccount().getBalanceForecast().add(request.getAmount()));

        scheduledRepository.save(st);
        userRepository.save(origin);
        userRepository.save(target);
    }
}