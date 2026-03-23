package com.daw.expenseIncomeManagerBack.getUserBalance.infrastructure;

import com.daw.expenseIncomeManagerBack.getUserBalance.application.UserBalanceDto;
import com.daw.expenseIncomeManagerBack.getUserBalance.domain.GetUserBalanceUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class GetUserBalanceController {

    private final GetUserBalanceUseCase getUserBalanceUseCase;

    public GetUserBalanceController(GetUserBalanceUseCase getUserBalanceUseCase) {
        this.getUserBalanceUseCase = getUserBalanceUseCase;
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<UserBalanceDto> getBalance(@PathVariable Long userId) {
        UserBalanceDto balanceDto = getUserBalanceUseCase.execute(userId);
        return ResponseEntity.ok(balanceDto);
    }
}