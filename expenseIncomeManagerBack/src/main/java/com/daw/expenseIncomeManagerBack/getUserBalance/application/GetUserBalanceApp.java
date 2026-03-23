package com.daw.expenseIncomeManagerBack.getUserBalance.application;

import com.daw.expenseIncomeManagerBack.getUserBalance.domain.GetUserBalanceUseCase;
import com.daw.expenseIncomeManagerBack.shared.domain.User;
import com.daw.expenseIncomeManagerBack.shared.domain.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class GetUserBalanceApp implements GetUserBalanceUseCase {

    private final UserRepository userRepository;

    public GetUserBalanceApp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserBalanceDto execute(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return new UserBalanceDto(user.getAccount().getBalance());
    }
}