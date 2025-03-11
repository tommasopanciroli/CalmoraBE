package com.example.Calmora.appointment;


import com.example.Calmora.auth.AppUserRepository;
import com.example.Calmora.psychologist.Psychologist;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Appuntamento> prenotaAppuntamento(
            @RequestParam Long psychologistId,
            @RequestParam Long patientId,
            @RequestParam LocalDateTime dataAppuntamento) {

        Appuntamento appuntamento = appuntamentoService.prenotaAppuntamento(psychologistId, patientId, dataAppuntamento);
        return ResponseEntity.ok(appuntamento);
    }

    // ottieni tutti gli appuntamenti di uno psicologo
    @GetMapping("/psychologist/{psychologistId}")
    public ResponseEntity<List<Appuntamento>> getAppuntamentiByPsychologist(@PathVariable Long psychologistId) {
        List<Appuntamento> appuntamenti = appuntamentoService.getAppuntamentiByPsychologist(psychologistId);
        return ResponseEntity.ok(appuntamenti);
    }

    // cancella un appuntamento
    @DeleteMapping("/{appuntamentoId}")
    public ResponseEntity<String> cancellaAppuntamento(@PathVariable Long appuntamentoId) {
        appuntamentoService.cancellaAppuntamento(appuntamentoId);
        return ResponseEntity.ok("Appuntamento eliminato con successo.");
    }

    // ottieni tutti gli appuntamenti di un paziente
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appuntamento>> getAppuntamentiByPatient(@PathVariable Long patientId) {
        List<Appuntamento> appuntamenti = appuntamentoService.getAppuntamentiByPatient(patientId);
        return ResponseEntity.ok(appuntamenti);
    }

}