package com.daw.expenseIncomeManagerBack.listMovements.application;

import com.daw.expenseIncomeManagerBack.listMovements.domain.ListMovementsUseCase;
import com.daw.expenseIncomeManagerBack.shared.domain.Movement;
import com.daw.expenseIncomeManagerBack.shared.domain.MovementRepository;
import com.daw.expenseIncomeManagerBack.shared.domain.ScheduledTransfer;
import com.daw.expenseIncomeManagerBack.shared.domain.ScheduledTransferRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListMovementsApp implements ListMovementsUseCase {

    private final MovementRepository movementRepository;
    private final ScheduledTransferRepository scheduledRepository;

    public ListMovementsApp(MovementRepository movementRepository, ScheduledTransferRepository scheduledRepository) {
        this.movementRepository = movementRepository;
        this.scheduledRepository = scheduledRepository;
    }

    @Override
    public List<MovementCalendarDto> execute(Long userId) {
        List<MovementCalendarDto> allEvents = new ArrayList<>();

        List<Movement> movements = movementRepository.findByUserId(userId);
        allEvents.addAll(movements.stream().map(this::mapMovementToDto).collect(Collectors.toList()));

        List<ScheduledTransfer> sentTransfers = scheduledRepository.findByOriginUserId(userId);
        sentTransfers.stream()
                .filter(st -> !st.isExecuted())
                .forEach(st -> allEvents.add(mapScheduledToDto(st, "EXPENSE")));

        List<ScheduledTransfer> receivedTransfers = scheduledRepository.findByTargetUserId(userId);
        receivedTransfers.stream()
                .filter(st -> !st.isExecuted())
                .forEach(st -> allEvents.add(mapScheduledToDto(st, "INCOME")));

        return allEvents;
    }

    private MovementCalendarDto mapMovementToDto(Movement movement) {
        MovementCalendarDto dto = new MovementCalendarDto();
        dto.setId(movement.getId().toString());
        dto.setDescription(movement.getDescription());
        dto.setAmount(movement.getAmount());
        dto.setType(movement.getType().name());
        dto.setStart(movement.getCreatedAt().toString());
        dto.setAttachedFileUrl(movement.getAttachedFileUrl());

        if (movement.getType().name().equals("INCOME")) {
            dto.setTitle(movement.getDescription() + " (+" + movement.getAmount() + "€)");
        } else {
            dto.setTitle(movement.getDescription() + " (-" + movement.getAmount() + "€)");
        }
        return dto;
    }

    private MovementCalendarDto mapScheduledToDto(ScheduledTransfer st, String type) {
        MovementCalendarDto dto = new MovementCalendarDto();
        dto.setId("SCH-" + st.getId());
        dto.setDescription(st.getDescription());
        dto.setAmount(st.getAmount());
        dto.setType(type);
        dto.setStart(st.getExecutionDate().toString());
        dto.setAttachedFileUrl(null);

        if (type.equals("INCOME")) {
            dto.setTitle("Recibirás de " + st.getOriginUser().getUsername() + " (+" + st.getAmount() + "€)");
        } else {
            dto.setTitle("Envío a " + st.getTargetUser().getUsername() + " (-" + st.getAmount() + "€)");
        }
        return dto;
    }
}