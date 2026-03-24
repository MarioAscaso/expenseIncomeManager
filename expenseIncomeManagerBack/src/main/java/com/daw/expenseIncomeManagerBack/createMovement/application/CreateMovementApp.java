package com.daw.expenseIncomeManagerBack.createMovement.application;

import com.daw.expenseIncomeManagerBack.createMovement.domain.CreateMovementUseCase;
import com.daw.expenseIncomeManagerBack.sendEmail.domain.SendEmailUseCase;
import com.daw.expenseIncomeManagerBack.shared.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class CreateMovementApp implements CreateMovementUseCase {

    private final MovementRepository movementRepository;
    private final UserRepository userRepository;
    private final SendEmailUseCase sendEmailUseCase;

    public CreateMovementApp(MovementRepository movementRepository, UserRepository userRepository, SendEmailUseCase sendEmailUseCase) {
        this.movementRepository = movementRepository;
        this.userRepository = userRepository;
        this.sendEmailUseCase = sendEmailUseCase;
    }

    @Override
    @Transactional
    public Movement execute(CreateMovementRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Movement movement = new Movement();
        movement.setDescription(request.getDescription());
        movement.setAmount(request.getAmount());
        movement.setType(request.getType());
        movement.setUser(user);

        if (request.getFile() != null && !request.getFile().isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + request.getFile().getOriginalFilename();
                Path path = Paths.get("uploads/" + fileName);
                Files.createDirectories(path.getParent());
                Files.write(path, request.getFile().getBytes());
                movement.setAttachedFileUrl("http://localhost:9393/uploads/" + fileName);
            } catch (Exception e) {
                throw new RuntimeException("Error al guardar el archivo.");
            }
        }

        if (request.getType().name().equals("INCOME")) {
            user.getAccount().setBalance(user.getAccount().getBalance().add(request.getAmount()));
        } else {
            user.getAccount().setBalance(user.getAccount().getBalance().subtract(request.getAmount()));
        }
        userRepository.save(user);
        Movement savedMovement = movementRepository.save(movement);

        if (user.getEmail() != null && user.getNotificationsEnabled()) {
            sendEmailUseCase.sendMovementNotification(
                    user.getEmail(),
                    savedMovement.getType().name(),
                    savedMovement.getAmount(),
                    savedMovement.getDescription()
            );
        }

        return savedMovement;
    }
}