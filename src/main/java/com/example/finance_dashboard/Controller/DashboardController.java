package com.example.finance_dashboard.Controller;

import com.example.finance_dashboard.Service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
  @Autowired
    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<?> getSummary(Principal principal) {
        return ResponseEntity.ok(dashboardService.getSummary(principal));
    }
}