package com.example.finance_dashboard.DTO.FinanceRecord;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MonthlyTrendResponse {
    private int month;
    private int year;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
}