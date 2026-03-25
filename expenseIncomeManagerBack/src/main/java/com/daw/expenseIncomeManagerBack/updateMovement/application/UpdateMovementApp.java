package com.daw.expenseIncomeManagerBack.updateMovement.application;

import com.daw.expenseIncomeManagerBack.shared.domain.*;
import com.daw.expenseIncomeManagerBack.updateMovement.domain.UpdateMovementUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class UpdateMovementApp implements UpdateMovementUseCase {

    private static final int MAX_EDIT_MINUTES = 5;

    private final MovementRepository movementRepository;
    private final UserRepository userRepository;
    private final FileStoragePort fileStoragePort;

    public UpdateMovementApp(MovementRepository movementRepository, UserRepository userRepository, FileStoragePort fileStoragePort) {
        this.movementRepository = movementRepository;
        this.userRepository = userRepository;
        this.fileStoragePort = fileStoragePort;
    }

    @Override
    @Transactional
    public Movement execute(Long id, UpdateMovementRequest request) {
        Movement movement = movementRepository.findById(id).orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));
        User user = movement.getUser();

        if (user.getRole() == RoleEnum.ADMIN) {
            throw new RuntimeException("Los administradores no tienen permisos para editar movimientos.");
        }

        if (user.getRole() == RoleEnum.BASIC) {
            long minutesPassed = Duration.between(movement.getCreatedAt(), LocalDateTime.now()).toMinutes();
            if (minutesPassed > MAX_EDIT_MINUTES) {
                throw new RuntimeException("Tiempo agotado. Solo se puede modificar un movimiento durante los primeros " + MAX_EDIT_MINUTES + " minutos.");
            }
        }

        if (movement.getType() == MovementTypeEnum.INCOME) {
            user.getAccount().setBalance(user.getAccount().getBalance().subtract(movement.getAmount()));
        } else {
            user.getAccount().setBalance(user.getAccount().getBalance().add(movement.getAmount()));
        }

        movement.setDescription(request.getDescription());
        movement.setAmount(request.getAmount());
        movement.setType(request.getType());

        if (movement.getType() == MovementTypeEnum.INCOME) {
            user.getAccount().setBalance(user.getAccount().getBalance().add(movement.getAmount()));
        } else {
            user.getAccount().setBalance(user.getAccount().getBalance().subtract(movement.getAmount()));
        }

        if (request.getFile() != null && !request.getFile().isEmpty()) {
            fileStoragePort.deleteFile(movement.getAttachedFileUrl());
            String newFileUrl = fileStoragePort.saveFile(request.getFile());
            movement.setAttachedFileUrl(newFileUrl);
        }

        userRepository.save(user);
        return movementRepository.save(movement);
    }
}