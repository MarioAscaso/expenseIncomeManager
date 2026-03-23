package com.daw.expenseIncomeManagerBack.shared.infrastructure.config;

import com.daw.expenseIncomeManagerBack.shared.domain.Account;
import com.daw.expenseIncomeManagerBack.shared.domain.RoleEnum;
import com.daw.expenseIncomeManagerBack.shared.domain.User;
import com.daw.expenseIncomeManagerBack.shared.domain.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println("Base de datos vacía. Creando usuarios y cuentas por defecto...");

            // --- SUPERADMIN ---
            User superAdmin = new User();
            superAdmin.setUsername("superadmin");
            superAdmin.setPassword(passwordEncoder.encode("1234"));
            superAdmin.setRole(RoleEnum.SUPERADMIN);
            superAdmin.setAccount(new Account(null, new BigDecimal("1500.00"))); // Enlazamos su cuenta

            // --- ADMIN ---
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("1234"));
            admin.setRole(RoleEnum.ADMIN);
            admin.setAccount(new Account(null, new BigDecimal("500.00"))); // Enlazamos su cuenta

            // --- USER ---
            User basicUser = new User();
            basicUser.setUsername("user");
            basicUser.setPassword(passwordEncoder.encode("1234"));
            basicUser.setRole(RoleEnum.BASIC);
            basicUser.setAccount(new Account(null, new BigDecimal("100.00"))); // Enlazamos su cuenta

            userRepository.save(superAdmin);
            userRepository.save(admin);
            userRepository.save(basicUser);

            System.out.println("¡Usuarios y Cuentas creados con éxito!");
        }
    }
}