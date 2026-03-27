package com.daw.expenseIncomeManagerBack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExpenseIncomeManagerBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpenseIncomeManagerBackApplication.class, args);
	}

}