package com.example.finance_dashboard.Repository;

import com.example.finance_dashboard.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {

}
