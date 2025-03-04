package com.example.Calmora.auth;

import com.example.Calmora.role.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String surname;
    private String email;
    private String password;
    private Role role;
    private String urlCertificato;
}
