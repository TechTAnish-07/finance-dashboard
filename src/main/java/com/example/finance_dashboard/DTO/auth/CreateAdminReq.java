package com.example.finance_dashboard.DTO.auth;

import com.example.finance_dashboard.DTO.Role;
import lombok.Data;

@Data
public class CreateAdminReq {
    private String email;
    private String password;
    private String organizationName;
    private Role role;
}
