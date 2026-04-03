package com.example.finance_dashboard.DTO.FinanceRecord;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateRecordReq {
    @NotNull(message = "User Email is required")
    @Email(message = "Invalid email format")
    private String userEmail;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Type is required")
    private Type type;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in future")
    private LocalDate date;

    private String description;
}
