package com.daw.expenseIncomeManagerBack.createMovement.application;

import com.daw.expenseIncomeManagerBack.createMovement.domain.CreateMovementUseCase;
import com.daw.expenseIncomeManagerBack.sendEmail.domain.SendEmailUseCase;
import com.daw.expenseIncomeManagerBack.shared.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class CreateMovementApp implements CreateMovementUseCase {

    private final MovementRepository movementRepository;
    private final UserRepository userRepository;
    private final SendEmailUseCase sendEmailUseCase;
    private final FileStoragePort fileStoragePort;

    public CreateMovementApp(MovementRepository movementRepository, UserRepository userRepository,
                             SendEmailUseCase sendEmailUseCase, FileStoragePort fileStoragePort) {
        this.movementRepository = movementRepository;
        this.userRepository = userRepository;
        this.sendEmailUseCase = sendEmailUseCase;
        this.fileStoragePort = fileStoragePort;
    }

    @Override
    @Transactional
    public Movement execute(CreateMovementRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Movement movement = new Movement();
        movement.setDescription(request.getDescription());
        movement.setAmount(request.getAmount());
        movement.setType(request.getType());
        movement.setUser(user);

        boolean isFuture = false;

        if (request.getDate() != null && !request.getDate().isEmpty()) {
            LocalDate movementDate = LocalDate.parse(request.getDate());
            movement.setCreatedAt(movementDate.atTime(LocalTime.now()));
            if (movementDate.isAfter(LocalDate.now())) {
                isFuture = true;
            }
        }

        if (request.getFile() != null && !request.getFile().isEmpty()) {
            String fileUrl = fileStoragePort.saveFile(request.getFile());
            movement.setAttachedFileUrl(fileUrl);
        }

        if (request.getType() == MovementTypeEnum.INCOME) {
            if (isFuture) {
                user.getAccount().setBalanceForecast(user.getAccount().getBalanceForecast().add(request.getAmount()));
            } else {
                user.getAccount().setBalance(user.getAccount().getBalance().add(request.getAmount()));
                user.getAccount().setBalanceForecast(user.getAccount().getBalanceForecast().add(request.getAmount()));
            }
        } else {
            if (isFuture) {
                user.getAccount().setBalanceForecast(user.getAccount().getBalanceForecast().subtract(request.getAmount()));
            } else {
                user.getAccount().setBalance(user.getAccount().getBalance().subtract(request.getAmount()));
                user.getAccount().setBalanceForecast(user.getAccount().getBalanceForecast().subtract(request.getAmount()));
            }
        }

        userRepository.save(user);
        Movement savedMovement = movementRepository.save(movement);

        if (user.getEmail() != null && user.getNotificationsEnabled()) {
            sendEmailUseCase.sendMovementNotification(
                    user.getEmail(), savedMovement.getType().name(),
                    savedMovement.getAmount(), savedMovement.getDescription()
            );
        }

        return savedMovement;
    }
}