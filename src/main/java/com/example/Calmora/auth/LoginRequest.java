package com.example.Calmora.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
