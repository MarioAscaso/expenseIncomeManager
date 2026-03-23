package com.daw.expenseIncomeManagerBack.auth.application;

import com.daw.expenseIncomeManagerBack.auth.domain.RegisterUseCase;
import com.daw.expenseIncomeManagerBack.shared.domain.Account;
import com.daw.expenseIncomeManagerBack.shared.domain.RoleEnum;
import com.daw.expenseIncomeManagerBack.shared.domain.User;
import com.daw.expenseIncomeManagerBack.shared.domain.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class RegisterApp implements RegisterUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterApp(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void execute(RegisterRequest request) {
        // 1. Comprobamos si el nombre de usuario ya existe
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        // 2. Creamos el nuevo usuario
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(RoleEnum.BASIC); // Rol básico por defecto

        // 3. Le creamos una cuenta asociada a 0€
        newUser.setAccount(new Account(null, BigDecimal.ZERO));

        // 4. Guardamos en BD (CascadeType.ALL guardará también la cuenta automáticamente)
        userRepository.save(newUser);
    }
}