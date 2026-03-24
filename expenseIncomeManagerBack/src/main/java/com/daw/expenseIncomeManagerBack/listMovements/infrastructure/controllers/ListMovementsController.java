package com.daw.expenseIncomeManagerBack.listMovements.infrastructure.controllers;

import com.daw.expenseIncomeManagerBack.listMovements.application.MovementCalendarDto;
import com.daw.expenseIncomeManagerBack.listMovements.domain.ListMovementsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movements")
@CrossOrigin(origins = "*")
public class ListMovementsController {

    private final ListMovementsUseCase listMovementsUseCase;

    public ListMovementsController(ListMovementsUseCase listMovementsUseCase) {
        this.listMovementsUseCase = listMovementsUseCase;
    }

    @GetMapping
    public ResponseEntity<List<MovementCalendarDto>> listMovements(@RequestParam Long userId) {
        List<MovementCalendarDto> movements = listMovementsUseCase.execute(userId);
        return ResponseEntity.ok(movements);
    }
}