package com.example.finance_dashboard.Service;

import com.example.finance_dashboard.DTO.Role;
import com.example.finance_dashboard.DTO.Status;
import com.example.finance_dashboard.DTO.auth.AuthResponse;
import com.example.finance_dashboard.DTO.auth.CreateAdminReq;
import com.example.finance_dashboard.DTO.auth.LoginRequest;
import com.example.finance_dashboard.DTO.auth.RefreshTokenRequest;
import com.example.finance_dashboard.Entity.User;
import com.example.finance_dashboard.Repository.UserRepo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final EmailService emailService;
    public AuthService(UserRepo userRepo,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       CustomUserDetailsService customUserDetailsService,
                       JwtService jwtService,
                       EmailService emailService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }


    public AuthResponse login(LoginRequest request) {
        if (request == null || isBlank(request.email()) || isBlank(request.password())) {
            throw new IllegalArgumentException("email and password are required");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().trim().toLowerCase(), request.password())
        );

        User user = userRepo.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("invalid credentials"));
         if(user.getStatus() != Status.ACTIVE) {
             throw new BadCredentialsException("User is not active");
         }
        String accessToken = jwtService.generateToken(
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        );
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken, "Bearer", user.getEmail(), user.getRole().name());
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        if (request == null || isBlank(request.refreshToken())) {
            throw new IllegalArgumentException("refreshToken is required");
        }

        String email = jwtService.extractEmail(request.refreshToken());
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        if (!jwtService.isRefreshTokenValid(request.refreshToken(), userDetails)) {
            throw new BadCredentialsException("invalid refresh token");
        }

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("user not found"));

        String accessToken = jwtService.generateToken(
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        );

        return new AuthResponse(accessToken, request.refreshToken(), "Bearer", user.getEmail(), user.getRole().name());
    }

    public String createAdmin(
            CreateAdminReq createAdminReq,
            Principal principal
    ){
        User user = userRepo.findByEmail(principal.getName()).orElseThrow(()
                -> new BadCredentialsException("user not found"));
        if(user.getRole() != Role.SUPERADMIN){
            throw new BadCredentialsException("invalid role");
        }

        User u1 = new User();
        u1.setEmail(createAdminReq.getEmail());
        u1.setRole(createAdminReq.getRole());
        userRepo.save(u1);
        u1.setStatus(Status.ACTIVE);
        emailService.sendLoginLink(u1.getId());

return "Successfully Admin Created";
    }
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
