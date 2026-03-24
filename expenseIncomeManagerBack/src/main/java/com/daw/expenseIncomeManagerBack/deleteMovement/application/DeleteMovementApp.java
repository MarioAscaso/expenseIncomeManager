package com.daw.expenseIncomeManagerBack.deleteMovement.application;

import com.daw.expenseIncomeManagerBack.deleteMovement.domain.DeleteMovementUseCase;
import com.daw.expenseIncomeManagerBack.shared.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.File;
import java.nio.file.Paths;

@Service
public class DeleteMovementApp implements DeleteMovementUseCase {
    private final MovementRepository movementRepository;
    private final UserRepository userRepository;

    public DeleteMovementApp(MovementRepository movementRepository, UserRepository userRepository) {
        this.movementRepository = movementRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void execute(Long id) {
        Movement m = movementRepository.findById(id).orElseThrow();

        if (m.getAttachedFileUrl() != null) {
            try {
                String name = m.getAttachedFileUrl().substring(m.getAttachedFileUrl().lastIndexOf("/") + 1);
                File f = Paths.get("uploads/" + name).toFile();
                if (f.exists()) f.delete();
            } catch (Exception e) { System.err.println("Error borrando físico"); }
        }

        User u = m.getUser();
        if (m.getType() == MovementTypeEnum.INCOME) u.getAccount().setBalance(u.getAccount().getBalance().subtract(m.getAmount()));
        else u.getAccount().setBalance(u.getAccount().getBalance().add(m.getAmount()));

        userRepository.save(u);
        movementRepository.delete(m);
    }
}