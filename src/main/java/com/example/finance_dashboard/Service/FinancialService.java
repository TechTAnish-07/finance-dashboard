package com.example.finance_dashboard.Service;

import com.example.finance_dashboard.DTO.FinanceRecord.CreateRecordReq;
import com.example.finance_dashboard.DTO.FinanceRecord.FinancialRecordResponse;
import com.example.finance_dashboard.DTO.Role;
import com.example.finance_dashboard.Entity.FinancialRecord;
import com.example.finance_dashboard.Entity.User;
import com.example.finance_dashboard.Repository.FinanceRepo;
import com.example.finance_dashboard.Repository.UserRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class FinancialService {
  private final UserRepo userRepo;
  private final FinanceRepo financeRepo;
  public FinancialService(UserRepo userRepo, FinanceRepo financeRepo) {
    this.userRepo = userRepo;
    this.financeRepo = financeRepo;
  }
    public String createRecord(CreateRecordReq req, Principal principal) {


        User admin = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        User targetUser = userRepo.findByEmail(req.getUserEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));


        if (!targetUser.getOrganizations().getId()
                .equals(admin.getOrganizations().getId())) {
            throw new RuntimeException("User does not belong to your organization");
        }


        FinancialRecord record = new FinancialRecord();
        record.setAmount(req.getAmount());
        record.setType(req.getType());
        record.setCategory(req.getCategory());
        record.setDate(req.getDate());
        record.setDescription(req.getDescription());
        record.setUser(targetUser);
        record.setCreatedBy(admin);
        record.setOrganization(admin.getOrganizations());
        record.setDeleted(false);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());

        financeRepo.save(record);

        return "Record created successfully";
    }
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
    public Page<FinancialRecordResponse> getRecords(
            Principal principal,
            String type,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            String search,
            Pageable pageable
    ) {
        User loggedInUser = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // VIEWER cannot filter
        if (loggedInUser.getRole() == Role.VIEWER) {
            if (type != null || category != null ||
                    startDate != null || endDate != null || search != null) {
                throw new RuntimeException(
                        "Viewers cannot filter or search records"
                );
            }
        }

        Page<FinancialRecord> records;

        if (loggedInUser.getRole() == Role.ADMIN) {
            records = financeRepo
                    .findByOrganization_IdAndIsDeletedFalse(
                            loggedInUser.getOrganizations().getId(),
                            pageable
                    );
        } else {
            records = financeRepo
                    .findByUserIdAndIsDeletedFalse(
                            loggedInUser.getId(),
                            pageable
                    );
        }

        return records.map(this::toResponse);
    }
}
