package com.example.finance_dashboard.DTO.FinanceRecord;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class FinancialRecordResponse {
    private Long id;
    private BigDecimal amount;
    private String type;
    private String category;
    private LocalDate date;
    private String description;
    private String belongsTo;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}