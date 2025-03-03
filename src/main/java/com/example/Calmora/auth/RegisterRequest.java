package com.example.Calmora.auth;

import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {
    private String name;
    private String surname;
    private String email;
    private String password;
    private Role role;
    private String urlCertificato;
}
