package com.example.finance_dashboard.Repository;

import com.example.finance_dashboard.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String adminEmail);
}
