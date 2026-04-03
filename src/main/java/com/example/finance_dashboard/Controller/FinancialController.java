package com.example.finance_dashboard.Controller;

import com.example.finance_dashboard.DTO.FinanceRecord.CreateRecordReq;
import com.example.finance_dashboard.DTO.FinanceRecord.FinancialRecordResponse;
import com.example.finance_dashboard.Service.FinancialService;
import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FinancialController {
    private final FinancialService financialService;
    public FinancialController(FinancialService financialService) {
        this.financialService = financialService;
    }
    @PostMapping("/create-record")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createFinancialRecord(
            @Valid @RequestBody CreateRecordReq req,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(financialService.createRecord(req, principal));
    }

    @GetMapping("/records")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST', 'VIEWER')")
    public ResponseEntity<?> getRecords(
            Principal principal,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(
                financialService.getRecords(
                        principal, type, category,
                        startDate, endDate, search, pageable
                )
        );
    }


}
