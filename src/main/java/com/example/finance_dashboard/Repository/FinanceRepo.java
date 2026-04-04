package com.example.finance_dashboard.Repository;

import com.example.finance_dashboard.DTO.FinanceRecord.Type;
import com.example.finance_dashboard.Entity.FinancialRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface FinanceRepo extends JpaRepository<FinancialRecord , Integer> {
    Page<FinancialRecord> findByOrganizationIdAndIsDeletedFalse(Long orgId, Pageable pageable );


    Page<FinancialRecord> findByUserIdAndIsDeletedFalse(Long userId , Pageable pageable);


        @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r " +
                "WHERE r.organization.id = :orgId " +
                "AND r.type = :type AND r.isDeleted = false")
        BigDecimal sumByOrgAndType(@Param("orgId") Long orgId,
                                   @Param("type") Type type);


        @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r " +
                "WHERE r.user.id = :userId " +
                "AND r.type = :type AND r.isDeleted = false")
        BigDecimal sumByUserAndType(@Param("userId") Long userId,
                                    @Param("type") Type type);


        @Query("SELECT r.category, SUM(r.amount) FROM FinancialRecord r " +
                "WHERE r.organization.id = :orgId AND r.isDeleted = false " +
                "GROUP BY r.category")
        List<Object[]> categoryTotalsByOrg(@Param("orgId") Long orgId);


        @Query("SELECT r.category, SUM(r.amount) FROM FinancialRecord r " +
                "WHERE r.user.id = :userId AND r.isDeleted = false " +
                "GROUP BY r.category")
        List<Object[]> categoryTotalsByUser(@Param("userId") Long userId);


        @Query("SELECT MONTH(r.date), YEAR(r.date), r.type, SUM(r.amount) " +
                "FROM FinancialRecord r " +
                "WHERE r.organization.id = :orgId AND r.isDeleted = false " +
                "GROUP BY YEAR(r.date), MONTH(r.date), r.type " +
                "ORDER BY YEAR(r.date), MONTH(r.date)")
        List<Object[]> monthlyTrendsByOrg(@Param("orgId") Long orgId);


        @Query("SELECT MONTH(r.date), YEAR(r.date), r.type, SUM(r.amount) " +
                "FROM FinancialRecord r " +
                "WHERE r.user.id = :userId AND r.isDeleted = false " +
                "GROUP BY YEAR(r.date), MONTH(r.date), r.type " +
                "ORDER BY YEAR(r.date), MONTH(r.date)")
        List<Object[]> monthlyTrendsByUser(@Param("userId") Long userId);


        Page<FinancialRecord> findByOrganization_IdAndIsDeletedFalse(
                Long orgId, Pageable pageable);


        Page<FinancialRecord> findByUser_IdAndIsDeletedFalse(
                Long userId, Pageable pageable);


        List<FinancialRecord> findTop10ByOrganization_IdAndIsDeletedFalse(
                Long orgId, Sort sort);


        List<FinancialRecord> findTop10ByUser_IdAndIsDeletedFalse(
                Long userId, Sort sort);
    }

