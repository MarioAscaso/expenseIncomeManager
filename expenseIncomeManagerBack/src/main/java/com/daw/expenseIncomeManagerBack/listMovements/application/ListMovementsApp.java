package com.daw.expenseIncomeManagerBack.listMovements.application;

import com.daw.expenseIncomeManagerBack.listMovements.domain.ListMovementsUseCase;
import com.daw.expenseIncomeManagerBack.shared.domain.Movement;
import com.daw.expenseIncomeManagerBack.shared.domain.MovementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListMovementsApp implements ListMovementsUseCase {

    private final MovementRepository movementRepository;

    public ListMovementsApp(MovementRepository movementRepository) {
        this.movementRepository = movementRepository;
    }

    @Override
    public List<MovementCalendarDto> execute(Long userId) {
        // Obtenemos los movimientos del usuario desde la BD
        List<Movement> movements = movementRepository.findByUserId(userId);

        // Los transformamos (mapeamos) uno a uno al formato de FullCalendar
        return movements.stream().map(this::mapToCalendarDto).collect(Collectors.toList());
    }

    // Método privado auxiliar para hacer la traducción
    private MovementCalendarDto mapToCalendarDto(Movement movement) {
        MovementCalendarDto dto = new MovementCalendarDto();
        dto.setId(movement.getId().toString());
        dto.setDescription(movement.getDescription());
        dto.setAmount(movement.getAmount());
        dto.setType(movement.getType().name());

        // FullCalendar necesita la fecha en string estándar ISO-8601
        dto.setStart(movement.getCreatedAt().toString());

        // Lógica visual: Si es ingreso verde, si es gasto rojo
        if (movement.getType().name().equals("INCOME")) {
            dto.setTitle(movement.getDescription() + " (+" + movement.getAmount() + "€)");
            dto.setBackgroundColor("#28a745"); // Verde Bootstrap
            dto.setBorderColor("#28a745");
        } else {
            dto.setTitle(movement.getDescription() + " (-" + movement.getAmount() + "€)");
            dto.setBackgroundColor("#dc3545"); // Rojo Bootstrap
            dto.setBorderColor("#dc3545");
        }
        dto.setAttachedFileUrl(movement.getAttachedFileUrl());
        return dto;
    }
}