package com.daw.expenseIncomeManagerBack.sendBizum.application;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BizumRequest {
    private Long originUserId;
    private String targetPhoneNumber;
    private BigDecimal amount;
    private String description;
}