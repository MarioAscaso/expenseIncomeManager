package com.daw.expenseIncomeManagerBack.loginUser.application;

import com.daw.expenseIncomeManagerBack.loginUser.domain.LoginUseCase;
import com.daw.expenseIncomeManagerBack.shared.domain.User;
import com.daw.expenseIncomeManagerBack.shared.domain.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginApp implements LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginApp(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponse execute(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        return new LoginResponse(user.getId(), user.getUsername(), user.getRole().name());
    }
}