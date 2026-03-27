package com.daw.expenseIncomeManagerBack.registerUser.application;

import com.daw.expenseIncomeManagerBack.registerUser.domain.RegisterUseCase;
import com.daw.expenseIncomeManagerBack.shared.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Random;

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

        Account newAccount = new Account();
        newAccount.setBalance(BigDecimal.ZERO);
        newAccount.setBalanceForecast(BigDecimal.ZERO);
        newAccount.setIban(generateSpanishIban());

        newUser.setAccount(newAccount);
        userRepository.save(newUser);
    }

    private String generateSpanishIban() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder("ES");
        for (int i = 0; i < 22; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}