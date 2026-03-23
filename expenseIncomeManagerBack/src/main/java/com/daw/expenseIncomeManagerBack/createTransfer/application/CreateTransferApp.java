package com.daw.expenseIncomeManagerBack.createTransfer.application;

import com.daw.expenseIncomeManagerBack.createTransfer.domain.CreateTransferUseCase;
import com.daw.expenseIncomeManagerBack.shared.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateTransferApp implements CreateTransferUseCase {

    private final UserRepository userRepository;
    private final MovementRepository movementRepository;

    public CreateTransferApp(UserRepository userRepository, MovementRepository movementRepository) {
        this.userRepository = userRepository;
        this.movementRepository = movementRepository;
    }

    @Override
    @Transactional
    public void execute(TransferRequest request) {
        User origin = userRepository.findById(request.getOriginUserId())
                .orElseThrow(() -> new RuntimeException("Usuario origen no encontrado"));

        User target = userRepository.findByUsername(request.getTargetUsername())
                .orElseThrow(() -> new RuntimeException("El usuario destino no existe"));

        if (origin.getId().equals(target.getId())) {
            throw new RuntimeException("No puedes enviarte dinero a ti mismo");
        }

        // CORRECCIÓN: Usar getAmount() en lugar de acceso directo
        if (origin.getAccount().getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Saldo insuficiente");
        }

        origin.getAccount().setBalance(origin.getAccount().getBalance().subtract(request.getAmount()));
        target.getAccount().setBalance(target.getAccount().getBalance().add(request.getAmount()));

        Movement expense = new Movement();
        expense.setDescription("Transferencia enviada a " + target.getUsername() + ": " + request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setType(MovementTypeEnum.EXPENSE);
        expense.setUser(origin);

        Movement income = new Movement();
        income.setDescription("Transferencia recibida de " + origin.getUsername() + ": " + request.getDescription());
        income.setAmount(request.getAmount());
        income.setType(MovementTypeEnum.INCOME);
        income.setUser(target);

        userRepository.save(origin);
        userRepository.save(target);
        movementRepository.save(expense);
        movementRepository.save(income);
    }
}