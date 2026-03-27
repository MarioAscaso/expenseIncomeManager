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

            User superAdmin = new User();
            superAdmin.setUsername("superadmin");
            superAdmin.setPassword(passwordEncoder.encode("1234"));
            superAdmin.setRole(RoleEnum.SUPERADMIN);
            superAdmin.setEmail("ascasovicentemario@gmail.com");
            superAdmin.setPhoneNumber("673849373");
            superAdmin.setAccount(new Account(null, new BigDecimal("1500.00"), new BigDecimal("1500.00"), "ES0112345678901234567890"));

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("1234"));
            admin.setRole(RoleEnum.ADMIN);
            admin.setEmail("bicimadmario@gmail.com");
            admin.setPhoneNumber("638463937");
            admin.setAccount(new Account(null, new BigDecimal("500.00"), new BigDecimal("500.00"), "ES0209876543210987654321"));

            User basicUser = new User();
            basicUser.setUsername("user");
            basicUser.setPassword(passwordEncoder.encode("1234"));
            basicUser.setRole(RoleEnum.BASIC);
            basicUser.setEmail("ascasomario27@gmail.com");
            basicUser.setPhoneNumber("600123456");
            basicUser.setAccount(new Account(null, new BigDecimal("100.00"), new BigDecimal("100.00"), "ES0355566677788899900011"));

            userRepository.save(superAdmin);
            userRepository.save(admin);
            userRepository.save(basicUser);

            System.out.println("¡Usuarios y Cuentas creados con éxito!");
        }
    }
}