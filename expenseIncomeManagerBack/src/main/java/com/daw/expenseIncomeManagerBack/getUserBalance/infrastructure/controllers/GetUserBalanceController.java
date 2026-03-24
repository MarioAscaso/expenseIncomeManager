package com.daw.expenseIncomeManagerBack.getUserBalance.infrastructure.controllers;

import com.daw.expenseIncomeManagerBack.getUserBalance.application.UserBalanceDto;
import com.daw.expenseIncomeManagerBack.getUserBalance.domain.GetUserBalanceUseCase;
import com.daw.expenseIncomeManagerBack.shared.domain.User;
import com.daw.expenseIncomeManagerBack.shared.domain.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class GetUserBalanceController {

    private final GetUserBalanceUseCase getUserBalanceUseCase;
    private final UserRepository userRepository;

    public GetUserBalanceController(GetUserBalanceUseCase getUserBalanceUseCase, UserRepository userRepository) {
        this.getUserBalanceUseCase = getUserBalanceUseCase;
        this.userRepository = userRepository;
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<UserBalanceDto> getBalance(@PathVariable Long userId) {
        UserBalanceDto balanceDto = getUserBalanceUseCase.execute(userId);
        return ResponseEntity.ok(balanceDto);
    }

    @PutMapping("/{userId}/toggle-notifications")
    public ResponseEntity<Boolean> toggleNotifications(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setNotificationsEnabled(!user.getNotificationsEnabled());
        userRepository.save(user);
        return ResponseEntity.ok(user.getNotificationsEnabled());
    }
}