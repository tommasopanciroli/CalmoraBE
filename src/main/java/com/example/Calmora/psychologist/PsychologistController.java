package com.example.Calmora.psychologist;

import com.example.Calmora.auth.AppUserRepository;
import com.example.Calmora.auth.AppUserService;
import com.example.Calmora.role.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/psychologists")
public class PsychologistController {

    private final AppUserRepository appUserRepository;
    private final AppUserService appUserService;

    public PsychologistController(AppUserRepository appUserRepository, AppUserService appUserService) {
        this.appUserRepository = appUserRepository;
        this.appUserService = appUserService;
    }

    @GetMapping
    public List<Psychologist> getAllPsychologists() {
        return appUserRepository.findAllByRole(Role.ROLE_PSYCHOLOGIST);
    }

    @GetMapping("/pending")
    public List<Psychologist> getPendingPsychologists() {
        return appUserService.getPendingPsychologists();
    }

    @PutMapping("/approve/{psychologistId}")
    public ResponseEntity<String> approvePsychologist(@PathVariable Long psychologistId) {
        appUserService.approvePsychologist(psychologistId);
        return ResponseEntity.ok("Psicologo approvato");
    }

}
