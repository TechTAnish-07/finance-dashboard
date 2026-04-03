package com.example.finance_dashboard.DTO.FinanceRecord;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardSummaryResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;
    private List<CategoryTotalResponse> categoryTotals;
    private List<FinancialRecordResponse> recentActivity;
    private List<MonthlyTrendResponse> monthlyTrends;
}
