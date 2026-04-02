package com.example.finance_dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FinanceDashboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanceDashboardApplication.class, args);
	}

}
