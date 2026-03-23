package com.daw.expenseIncomeManagerBack.auth.application;

import com.daw.expenseIncomeManagerBack.auth.domain.LoginUseCase;
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
        // 1. Buscamos el usuario por su username
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));

        // 2. Comprobamos que la contraseña sea correcta (se compara la plana con la encriptada)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        // 3. Devolvemos los datos del usuario logueado (sin la contraseña, por seguridad)
        return new LoginResponse(user.getId(), user.getUsername(), user.getRole().name());
    }
}