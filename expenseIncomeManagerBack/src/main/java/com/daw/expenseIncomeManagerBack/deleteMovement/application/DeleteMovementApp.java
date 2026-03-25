package com.daw.expenseIncomeManagerBack.deleteMovement.application;

import com.daw.expenseIncomeManagerBack.deleteMovement.domain.DeleteMovementUseCase;
import com.daw.expenseIncomeManagerBack.shared.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteMovementApp implements DeleteMovementUseCase {

    private final MovementRepository movementRepository;
    private final UserRepository userRepository;
    private final FileStoragePort fileStoragePort;

    public DeleteMovementApp(MovementRepository movementRepository, UserRepository userRepository, FileStoragePort fileStoragePort) {
        this.movementRepository = movementRepository;
        this.userRepository = userRepository;
        this.fileStoragePort = fileStoragePort;
    }

    @Override
    @Transactional
    public void execute(Long id) {
        Movement movement = movementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));

        User user = movement.getUser();

        if (movement.getType() == MovementTypeEnum.INCOME) {
            user.getAccount().setBalance(user.getAccount().getBalance().subtract(movement.getAmount()));
        } else {
            user.getAccount().setBalance(user.getAccount().getBalance().add(movement.getAmount()));
        }

        if (movement.getAttachedFileUrl() != null) {
            fileStoragePort.deleteFile(movement.getAttachedFileUrl());
        }

        userRepository.save(user);
        movementRepository.delete(movement);
    }
}