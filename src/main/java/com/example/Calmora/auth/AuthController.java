package com.example.Calmora.auth;

import com.example.Calmora.psychologist.Psychologist;
import com.example.Calmora.role.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AppUserService appUserService;
    private final AppUserRepository appUserRepository;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        appUserService.registerUser(
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                registerRequest.getRole(),
                registerRequest.getName(),
                registerRequest.getSurname(),
                registerRequest.getUrlCertificato()
        );
        return ResponseEntity.ok("Registrazione avvenuta con successo");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        String token = appUserService.authenticateUser(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @GetMapping("/patients")
    @PreAuthorize("hasRole('ROLE_PSYCHOLOGIST')")
    public List<AppUser> getAllPatient() {
        return appUserRepository.findPatientByRole(Role.ROLE_USER);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_USER', 'ROLE_ADMIN')")
    public List<Psychologist> searchPsychologists(@RequestParam String keyword) {
        return appUserService.searchPsychologists(keyword);
    }
}