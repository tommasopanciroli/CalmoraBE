package com.example.Calmora.auth;

import com.example.Calmora.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthRunner implements ApplicationRunner {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Creazione dell'utente admin se non esiste
        Optional<AppUser> adminUser = appUserService.findByEmail("admin@example.com");
        if (adminUser.isEmpty()) {
            appUserService.registerUser(
                    "admin@example.com",
                    "adminpwd",
                    Role.ROLE_ADMIN,
                    "Admin",
                    "Superuser",
                    null
            );
        }

    }
}