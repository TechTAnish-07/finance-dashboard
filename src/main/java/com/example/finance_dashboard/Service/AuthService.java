package com.example.finance_dashboard.Service;

import com.example.finance_dashboard.DTO.Role;
import com.example.finance_dashboard.DTO.Status;
import com.example.finance_dashboard.DTO.auth.*;
import com.example.finance_dashboard.Entity.Organizations;
import com.example.finance_dashboard.Entity.User;
import com.example.finance_dashboard.Repository.OrganisationRepo;
import com.example.finance_dashboard.Repository.UserRepo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepo userRepo;
    private final OrganisationRepo organisationRepo;
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
                       EmailService emailService,
                       OrganisationRepo organisationRepo
                       ) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.organisationRepo = organisationRepo;
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
        String tempPassword = UUID.randomUUID().toString().substring(0, 8);

        Organizations org = new Organizations();
        org.setName(createAdminReq.getOrganizationName());
        org.setStatus(Status.ACTIVE);
        org.setCreatedDate(LocalDateTime.now());
        organisationRepo.save(org);


        User u1 = new User();
        u1.setEmail(createAdminReq.getEmail());
        u1.setName(createAdminReq.getName());
        u1.setOrganizations(org);
        u1.setPassword(passwordEncoder.encode(tempPassword));
        u1.setRole(Role.ADMIN);
        u1.setStatus(Status.ACTIVE);
        u1.setCreatedAt(LocalDateTime.now());
        u1.setUpdatedAt(LocalDateTime.now());
        userRepo.save(u1);
        emailService.sendLoginLink(u1.getId() , tempPassword);

     return "Successfully Admin Created";
    }

    public String createUser(CreateUserReq req, Principal principal) {


        User admin = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new BadCredentialsException("Admin not found"));

        if (admin.getRole() != Role.ADMIN) {
            throw new BadCredentialsException("Only ADMIN can create users");
        }


        Organizations org = admin.getOrganizations();
        if (org == null) {
            throw new RuntimeException("Admin has no organization assigned");
        }

         if (req.getRole() == Role.ADMIN || req.getRole() == Role.SUPERADMIN) {
            throw new BadCredentialsException("ADMIN can only create ANALYST or VIEWER");
        }

        if (userRepo.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already exists");
        }


        String tempPassword = UUID.randomUUID().toString().substring(0, 8);


        User newUser = new User();
        newUser.setEmail(req.getEmail());
        newUser.setName(req.getName());
        newUser.setPassword(passwordEncoder.encode(tempPassword)); // hashed in DB
        newUser.setRole(req.getRole());
        newUser.setStatus(Status.ACTIVE);
        newUser.setOrganizations(org);
        newUser.setIsFirstLogin(true);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        userRepo.save(newUser);


        emailService.sendLoginLink(newUser.getId(), tempPassword);

        return "User created successfully";
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
