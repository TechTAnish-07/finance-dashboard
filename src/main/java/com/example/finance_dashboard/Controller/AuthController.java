package com.example.finance_dashboard.Controller;

import com.example.finance_dashboard.DTO.auth.AuthResponse;
import com.example.finance_dashboard.DTO.auth.CreateAdminReq;
import com.example.finance_dashboard.DTO.auth.LoginRequest;
import com.example.finance_dashboard.DTO.auth.RefreshTokenRequest;
import com.example.finance_dashboard.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }



    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

//      // have to update
//    @PostMapping("/create-user")
//    public ResponseEntity<String>createUser(@RequestBody CreateAdminReq createAdminReq,
//                                        Principal principal){
//
//        return ResponseEntity.ok(authService.createAdmin(createAdminReq, principal));
//
//    }
//


    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }
}
