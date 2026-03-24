package com.daw.expenseIncomeManagerBack.sendBizum.infrastructure.controllers;

import com.daw.expenseIncomeManagerBack.sendBizum.application.BizumRequest;
import com.daw.expenseIncomeManagerBack.sendBizum.domain.SendBizumUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bizum")
@CrossOrigin(origins = "*")
public class SendBizumController {

    private final SendBizumUseCase sendBizumUseCase;

    public SendBizumController(SendBizumUseCase sendBizumUseCase) {
        this.sendBizumUseCase = sendBizumUseCase;
    }

    @PostMapping
    public ResponseEntity<?> sendBizum(@RequestBody BizumRequest request) {
        sendBizumUseCase.execute(request);
        return ResponseEntity.ok().body("{\"message\": \"Bizum enviado con éxito\"}");
    }
}