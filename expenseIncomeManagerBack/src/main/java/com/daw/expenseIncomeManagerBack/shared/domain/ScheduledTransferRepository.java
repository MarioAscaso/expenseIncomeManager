package com.daw.expenseIncomeManagerBack.shared.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduledTransferRepository extends JpaRepository<ScheduledTransfer, Long> {
    List<ScheduledTransfer> findByExecutionDateAndExecutedFalse(LocalDate date);

    List<ScheduledTransfer> findByOriginUserId(Long originUserId);
    List<ScheduledTransfer> findByTargetUserId(Long targetUserId);
}