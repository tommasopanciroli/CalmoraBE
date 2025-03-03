package com.example.Calmora.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

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

        // Creazione dell'utente user se non esiste
        Optional<AppUser> normalUser = appUserService.findByEmail("user@example.com");
        if (normalUser.isEmpty()) {
            appUserService.registerUser(
                    "user@example.com",
                    "userpwd",
                    Role.ROLE_USER,
                    "User",
                    "Example",
                    null
            );
        }

        // Creazione dell'utente psychologist se non esiste
        Optional<AppUser> psychologistUser = appUserService.findByEmail("psychologist@example.com");
        if (psychologistUser.isEmpty()) {
            appUserService.registerUser(
                    "psychologist@example.com",
                    "psychologistpwd",
                    Role.ROLE_PSYCHOLOGIST,
                    "Psychologist",
                    "Therapist",
                    "https://example.com/certificato.pdf"
            );
        }
    }
}