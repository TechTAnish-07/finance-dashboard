package com.example.finance_dashboard.Service;


import com.example.finance_dashboard.Entity.User;
import com.example.finance_dashboard.Repository.UserRepo;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepo userRepo;

    public CustomUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())

                .roles(user.getRole().name())
                .build();
    }

    public UserDetails save(User user) {
        return userRepo.save(user);
    }

    public User getUserFromPrincipal(Principal principal) {
        if (principal == null) {
            throw new AccessDeniedException("Unauthenticated");
        }

        return userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
