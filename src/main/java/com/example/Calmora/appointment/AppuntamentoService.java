package com.example.Calmora.appointment;

import com.example.Calmora.auth.AppUser;
import com.example.Calmora.auth.AppUserRepository;
import com.example.Calmora.email.EmailService;
import com.example.Calmora.psychologist.Psychologist;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppuntamentoService {

    @Autowired
    private AppuntamentoRepository appuntamentoRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private EmailService emailService;

    // Prenota un appuntamento
    public Appuntamento prenotaAppuntamento(Long psychologistId, Long patientId, LocalDateTime dataAppuntamento) {
        LocalDateTime dataFine = dataAppuntamento.plusHours(1); // Durata di 1 ora

        // Recupera psicologo e paziente
        AppUser psychologistUser = appUserRepository.findById(psychologistId)
                .orElseThrow(() -> new EntityNotFoundException("Psicologo non trovato"));

        if (!(psychologistUser instanceof Psychologist)) {
            throw new IllegalArgumentException("L'utente selezionato non è uno psicologo");
        }
        Psychologist psychologist = (Psychologist) psychologistUser;

        AppUser patient = appUserRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Paziente non trovato"));

        // Verifica se lo psicologo ha già un appuntamento in questa fascia oraria
        List<Appuntamento> conflitti = appuntamentoRepository.findConflictingAppointments(
                psychologist,
                dataAppuntamento,
                dataFine
        );

        if (!conflitti.isEmpty()) {
            throw new IllegalStateException("Lo psicologo non è disponibile in questo orario");
        }

        // crea e salva l'appuntamento
        Appuntamento appuntamento = new Appuntamento(psychologist, patient, dataAppuntamento, dataFine);
        appuntamentoRepository.save(appuntamento);

        // invia email al paziente con il link Meet dello psicologo
        String subject = "Conferma appuntamento con " + psychologist.getName() + " " + psychologist.getSurname();
        String message = """
                Ciao %s,
                Il tuo appuntamento con lo psicologo %s %s è stato confermato!
                
                📅 Data e ora: %s
                
                🔗 Partecipa alla chiamata Meet: %s
                
                A presto!
                """.formatted(
                patient.getName(),
                psychologist.getName(),
                psychologist.getSurname(),
                dataAppuntamento,
                psychologist.getUrlMeet()
        );

        emailService.sendEmail(patient.getEmail(), subject, message);

        return appuntamento;
    }

    // Ottieni tutti gli appuntamenti di uno psicologo
    public List<Appuntamento> getAppuntamentiByPsychologist(Psychologist psychologist) {
        return appuntamentoRepository.findByPsicologo(psychologist);
    }

    // ottieni tutti gli appuntmamenti di un paziente
    public List<Appuntamento> getAppuntamentiByPatient(Long patientId) {
        return appuntamentoRepository.findByPazienteId(patientId);
    }

    // cancella un appuntamento
    public void cancellaAppuntamento(Long appuntamentoId) {
        if (!appuntamentoRepository.existsById(appuntamentoId)) {
            throw new EntityNotFoundException("Appuntamento non trovato");
        }
        appuntamentoRepository.deleteById(appuntamentoId);
    }
}