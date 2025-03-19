package com.example.Calmora.appointment;


import com.example.Calmora.auth.AppUserRepository;
import com.example.Calmora.psychologist.Psychologist;
import com.example.Calmora.security.CustomUserDetails;
import com.example.Calmora.security.CustomUserDetailsService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppuntamentoController {

    private final AppuntamentoService appuntamentoService;
    private final AppUserRepository appUserRepository;

    public AppuntamentoController(AppuntamentoService appuntamentoService, AppUserRepository appUserRepository) {
        this.appuntamentoService = appuntamentoService;
        this.appUserRepository = appUserRepository;
    }

    // prenota un appuntamento
    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Appuntamento> prenotaAppuntamento(
            @RequestParam Long psychologistId,
            @RequestParam LocalDateTime dataAppuntamento,
            Authentication authentication) {

        // recupera l'utente autenticato e il suo ID
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long patientId = userDetails.getId();

        Appuntamento appuntamento = appuntamentoService.prenotaAppuntamento(psychologistId, patientId, dataAppuntamento);
        return ResponseEntity.ok(appuntamento);
    }

    // ottieni tutti gli appuntamenti di uno psicologo
    @GetMapping("/psychologist/my")
    @PreAuthorize("hasRole('ROLE_PSYCHOLOGIST')")
    public ResponseEntity<List<Appuntamento>> getAppuntamentiByPsychologist(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long psychologistId = userDetails.getId();
        List<Appuntamento> appuntamenti = appuntamentoService.getAppuntamentiByPsychologist(psychologistId);
        return ResponseEntity.ok(appuntamenti);
    }

    // cancella un appuntamento
    @DeleteMapping("/{appuntamentoId}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_PSYCHOLOGIST')")
    public ResponseEntity<String> cancellaAppuntamento(@PathVariable Long appuntamentoId, Authentication authentication) {
        appuntamentoService.cancellaAppuntamento(appuntamentoId);
        return ResponseEntity.ok("Appuntamento eliminato con successo.");
    }

    // ottieni tutti gli appuntamenti di un paziente
    @GetMapping("/my")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<Appuntamento>> getAppuntamentiDelMioProfilo(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long patientId = userDetails.getId();
        List<Appuntamento> appuntamenti = appuntamentoService.getAppuntamentiByPatient(patientId);
        return ResponseEntity.ok(appuntamenti);
    }

}