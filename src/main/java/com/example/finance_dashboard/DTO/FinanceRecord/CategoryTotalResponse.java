package com.example.finance_dashboard.DTO.FinanceRecord;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CategoryTotalResponse {
    private String category;
    private BigDecimal total;
}