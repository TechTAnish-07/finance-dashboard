package com.example.finance_dashboard.Config;

import com.example.finance_dashboard.DTO.Role;
import com.example.finance_dashboard.DTO.Status;
import com.example.finance_dashboard.Entity.User;
import com.example.finance_dashboard.Repository.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

        @Bean
        public CommandLineRunner initAdmin(UserRepo repo, PasswordEncoder encoder) {
            return args -> {

                String adminEmail = "patidartanish31@gmail.com";

                if (repo.existsByEmail(adminEmail)) {
                    System.out.println("✔ SuperAdmin already exists");
                    return;
                }

                User admin = new User(
                        adminEmail,
                        "Tanish",
                        encoder.encode("9165849391"),
                        Role.SUPERADMIN,
                        Status.ACTIVE
                );

                // SUPERADMIN has no org
                admin.setOrganizations(null);

                repo.save(admin);
                System.out.println("✔ SuperAdmin created successfully");
            };
        }
    }