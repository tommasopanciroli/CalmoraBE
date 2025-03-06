package com.example.Calmora.google;

import com.example.Calmora.appointment.Appuntamento;
import com.example.Calmora.appointment.AppuntamentoService;
import com.example.Calmora.auth.AppUser;
import com.example.Calmora.auth.AppUserRepository;
import com.example.Calmora.psychologist.Psychologist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private AppuntamentoService appuntamentoService;

    @Autowired
    private AppUserRepository appUserRepository;

    @PostMapping("/addEvent")
    public String createCalendarEvent(
            @RequestParam Long psychologistId,
            @RequestParam Long patientId,
            @RequestParam LocalDateTime dataAppuntamento
    ) {
        try {
            // Recupera lo psicologo e il paziente dal database
            AppUser psychologistUser = appUserRepository.findById(psychologistId)
                    .orElseThrow(() -> new IllegalArgumentException("Psicologo non trovato con ID: " + psychologistId));

            if (!(psychologistUser instanceof Psychologist)) {
                throw new IllegalArgumentException("L'utente con ID " + psychologistId + " non è uno psicologo");
            }
            Psychologist psychologist = (Psychologist) psychologistUser;

            AppUser patient = appUserRepository.findById(patientId)
                    .orElseThrow(() -> new IllegalArgumentException("Paziente non trovato con ID: " + patientId));

            // Prenota l'appuntamento nel database
            Appuntamento appuntamento = appuntamentoService.prenotaAppuntamento(psychologist.getId(), patient.getId(), dataAppuntamento);

            // Registra l'appuntamento su Google Calendar
            String calendarEventLink = calendarService.addEventToCalendar(appuntamento);

// Log di conferma
            System.out.println("✅ Evento creato su Google Calendar: " + calendarEventLink);

            return "Evento creato con successo su Google Calendar!";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "Errore: " + e.getMessage();
        }
    }
}
