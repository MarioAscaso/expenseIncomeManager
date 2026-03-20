package com.daw.expenseIncomeManagerBack.listMovements.domain;

import com.daw.expenseIncomeManagerBack.listMovements.application.MovementCalendarDto;
import java.util.List;

public interface ListMovementsUseCase {
    List<MovementCalendarDto> execute(Long userId);
}