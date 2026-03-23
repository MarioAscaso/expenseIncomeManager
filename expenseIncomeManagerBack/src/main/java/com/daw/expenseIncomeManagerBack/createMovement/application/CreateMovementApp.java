package com.daw.expenseIncomeManagerBack.createMovement.application;

import com.daw.expenseIncomeManagerBack.createMovement.domain.CreateMovementUseCase;
import com.daw.expenseIncomeManagerBack.shared.domain.Movement;
import com.daw.expenseIncomeManagerBack.shared.domain.MovementRepository;
import com.daw.expenseIncomeManagerBack.shared.domain.User;
import com.daw.expenseIncomeManagerBack.shared.domain.UserRepository;
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

    public CreateMovementApp(MovementRepository movementRepository, UserRepository userRepository) {
        this.movementRepository = movementRepository;
        this.userRepository = userRepository;
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

        // --- LÓGICA DE GUARDADO DE ARCHIVO ---
        if (request.getFile() != null && !request.getFile().isEmpty()) {
            try {
                // Generamos un nombre único para que no se sobreescriban archivos
                String fileName = UUID.randomUUID().toString() + "_" + request.getFile().getOriginalFilename();
                Path path = Paths.get("uploads/" + fileName);

                // Creamos la carpeta si no existe
                Files.createDirectories(path.getParent());

                // Guardamos el archivo en el disco
                Files.write(path, request.getFile().getBytes());

                // Guardamos la URL pública en la base de datos
                movement.setAttachedFileUrl("http://localhost:9393/uploads/" + fileName);
            } catch (Exception e) {
                throw new RuntimeException("Error al guardar el archivo: " + e.getMessage());
            }
        }

        // --- LÓGICA DEL SALDO EN LA CUENTA (1 A 1) ---
        if (request.getType().name().equals("INCOME")) {
            user.getAccount().setBalance(user.getAccount().getBalance().add(request.getAmount()));
        } else {
            user.getAccount().setBalance(user.getAccount().getBalance().subtract(request.getAmount()));
        }
        userRepository.save(user);

        return movementRepository.save(movement);
    }
}