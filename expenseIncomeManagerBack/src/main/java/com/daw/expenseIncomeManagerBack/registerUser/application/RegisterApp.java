package com.daw.expenseIncomeManagerBack.registerUser.application;

import com.daw.expenseIncomeManagerBack.registerUser.domain.RegisterUseCase;
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
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setEmail(request.getEmail());
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setRole(RoleEnum.BASIC);

        newUser.setAccount(new Account(null, BigDecimal.ZERO));

        userRepository.save(newUser);
    }
}