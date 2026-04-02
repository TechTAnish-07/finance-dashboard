package com.example.finance_dashboard.Repository;


import com.example.finance_dashboard.Entity.Organizations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganisationRepo extends JpaRepository<Organizations ,Integer> {
}
