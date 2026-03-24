package com.daw.expenseIncomeManagerBack.updateMovement.application;

import com.daw.expenseIncomeManagerBack.shared.domain.Movement;
import com.daw.expenseIncomeManagerBack.shared.domain.MovementRepository;
import com.daw.expenseIncomeManagerBack.shared.domain.MovementTypeEnum;
import com.daw.expenseIncomeManagerBack.shared.domain.RoleEnum;
import com.daw.expenseIncomeManagerBack.shared.domain.User;
import com.daw.expenseIncomeManagerBack.shared.domain.UserRepository;
import com.daw.expenseIncomeManagerBack.updateMovement.domain.UpdateMovementUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class UpdateMovementApp implements UpdateMovementUseCase {

    private final MovementRepository movementRepository;
    private final UserRepository userRepository;

    public UpdateMovementApp(MovementRepository movementRepository, UserRepository userRepository) {
        this.movementRepository = movementRepository;
        this.userRepository = userRepository;
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
            if (minutesPassed > 5) {
                throw new RuntimeException("Tiempo agotado. Por seguridad, no se puede modificar un movimiento pasados 5 minutos.");
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
            try {
                if (movement.getAttachedFileUrl() != null) {
                    String oldFileName = movement.getAttachedFileUrl().substring(movement.getAttachedFileUrl().lastIndexOf("/") + 1);
                    java.nio.file.Paths.get("uploads/" + oldFileName).toFile().delete();
                }

                String newFileName = java.util.UUID.randomUUID().toString() + "_" + request.getFile().getOriginalFilename();
                java.nio.file.Path path = java.nio.file.Paths.get("uploads/" + newFileName);
                java.nio.file.Files.createDirectories(path.getParent());
                java.nio.file.Files.write(path, request.getFile().getBytes());

                movement.setAttachedFileUrl("http://localhost:9393/uploads/" + newFileName);
            } catch (Exception e) {
                throw new RuntimeException("Error al actualizar el archivo.");
            }
        }

        userRepository.save(user);
        return movementRepository.save(movement);
    }
}