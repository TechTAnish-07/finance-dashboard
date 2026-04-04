package com.example.finance_dashboard.Service;


import com.example.finance_dashboard.DTO.FinanceRecord.*;
import com.example.finance_dashboard.DTO.Role;
import com.example.finance_dashboard.Entity.FinancialRecord;
import com.example.finance_dashboard.Entity.User;
import com.example.finance_dashboard.Repository.FinanceRepo;
import com.example.finance_dashboard.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinanceRepo financialRecordRepo;
    private final UserRepo userRepo;


    private FinancialRecordResponse toResponse(FinancialRecord r) {
        return FinancialRecordResponse.builder()
                .id(r.getId())
                .amount(r.getAmount())
                .type(r.getType().name())
                .category(r.getCategory())
                .date(r.getDate())
                .description(r.getDescription())
                .belongsTo(r.getUser().getName())
                .createdBy(r.getCreatedBy().getName())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }



    public DashboardSummaryResponse getSummary(Principal principal) {




        User loggedInUser = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = loggedInUser.getRole() == Role.ADMIN;
        Long orgId = isAdmin ? loggedInUser.getOrganizations().getId() : null;
        Long userId = isAdmin ? null : loggedInUser.getId();

        
        BigDecimal totalIncome = isAdmin
                ? financialRecordRepo.sumByOrgAndType(orgId, Type.INCOME)
                : financialRecordRepo.sumByUserAndType(userId, Type.INCOME);

       
        BigDecimal totalExpenses = isAdmin
                ? financialRecordRepo.sumByOrgAndType(orgId, Type.EXPENSE)
                : financialRecordRepo.sumByUserAndType(userId, Type.EXPENSE);

      
        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

       
        List<Object[]> rawCategories = isAdmin
                ? financialRecordRepo.categoryTotalsByOrg(orgId)
                : financialRecordRepo.categoryTotalsByUser(userId);

        List<CategoryTotalResponse> categoryTotals = rawCategories.stream()
                .map(row -> CategoryTotalResponse.builder()
                        .category(row[0].toString())
                        .total((BigDecimal) row[1])
                        .build())
                .collect(Collectors.toList());

       
        List<Object[]> rawTrends = isAdmin
                ? financialRecordRepo.monthlyTrendsByOrg(orgId)
                : financialRecordRepo.monthlyTrendsByUser(userId);

        // Group by month+year
        Map<String, MonthlyTrendResponse> trendMap = new LinkedHashMap<>();
        for (Object[] row : rawTrends) {
            int month = ((Number) row[0]).intValue();
            int year  = ((Number) row[1]).intValue();
            Type type = Type.valueOf(row[2].toString());
            BigDecimal amount = (BigDecimal) row[3];

            String key = year + "-" + month;
            trendMap.putIfAbsent(key, MonthlyTrendResponse.builder()
                    .month(month).year(year)
                    .totalIncome(BigDecimal.ZERO)
                    .totalExpenses(BigDecimal.ZERO)
                    .build());

            MonthlyTrendResponse trend = trendMap.get(key);
            if (type == Type.INCOME) {
                trend.setTotalIncome(amount);
            } else {
                trend.setTotalExpenses(amount);
            }
        }
        List<MonthlyTrendResponse> monthlyTrends =
                new ArrayList<>(trendMap.values());


        List<FinancialRecord> recentRecords = isAdmin
                ? financialRecordRepo
                .findTop10ByOrganization_IdAndIsDeletedFalse(
                        orgId, Sort.by("date").descending())
                : financialRecordRepo
                .findTop10ByOrganization_IdAndIsDeletedFalse(
                        userId, Sort.by("date").descending());

        List<FinancialRecordResponse> recentActivity = recentRecords.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());


        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .categoryTotals(categoryTotals)
                .recentActivity(recentActivity)
                .monthlyTrends(monthlyTrends)
                .build();
    }


}