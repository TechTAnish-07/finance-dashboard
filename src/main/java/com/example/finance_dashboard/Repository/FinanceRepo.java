package com.example.finance_dashboard.Repository;

import com.example.finance_dashboard.Entity.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinanceRepo extends JpaRepository<FinancialRecord , Integer> {
}
