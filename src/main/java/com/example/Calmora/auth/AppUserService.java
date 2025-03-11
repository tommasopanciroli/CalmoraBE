package com.example.Calmora.auth;

import com.example.Calmora.email.EmailService;
import com.example.Calmora.google.MeetService;
import com.example.Calmora.psychologist.Psychologist;
import com.example.Calmora.role.Role;
import com.example.Calmora.security.JwtTokenUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private MeetService meetService;

    @Autowired
    private EmailService emailService;

    // Registrazione di un nuovo utente
    public AppUser registerUser(String email, String password, Role role, String name, String surname, String urlCertificato) {
        if (appUserRepository.existsByEmail(email)) {
            throw new EntityExistsException("Email già in uso");
        }

        AppUser user;
        String encodedPassword = passwordEncoder.encode(password);

        if (role == Role.ROLE_PSYCHOLOGIST) {
            if (urlCertificato == null || urlCertificato.isBlank()) {
                throw new IllegalArgumentException("Gli psicologi devono fornire un certificato valido");
            }
            String meetLink = meetService.createMeetLink(email);
            user = new Psychologist(name, surname, email, encodedPassword, urlCertificato, meetLink);
            String subject = "Registrazione in Calmora avvenuta con successo";
            String message = """
                    Ciao %s %s,
                    
                    La registrazione alla piattaforma Calmora è avvenuta con successo.
                    
                    Il tuo account in questo momento è in attesa di approvazione da parte dei nostri amministratori.
                    Ti avviseremo non appena sarà attivato.
                    
                    Nel frattempo, ecco il tuo link per accedere alla stanza virtuale per i tuoi appuntamenti:
                            %s
                    
                    Grazie per aver scelto Calmora!
                    
                    Cordiali saluti,
                    Il team di Calmora
                    """.formatted(name, surname, meetLink);


            emailService.sendEmail(email, subject, message);
        } else {
            user = new AppUser(name, surname, email, encodedPassword, role);
            String subject = "Registrazione in Calmora avvenuta con successo";
            String message = """
                    Ciao %s %s,
                    
                    La registrazione alla piattaforma Calmora è avvenuta con successo.
                    Puoi accedere alla piattaforma con le credenziali che hai inserito.  
                    
                    Grazie per aver scelto Calmora, ci impegneremo ogni giorno per garantirti il miglior supporto possibile.
                    
                    Cordiali saluti,
                    Il team di Calmora
                    """.formatted(name, surname);

            emailService.sendEmail(email, subject, message);
        }

        return appUserRepository.save(user);
    }


    public Optional<AppUser> findByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    // Autenticazione di un utente e generazione del token JWT
    public String authenticateUser(String email, String password) {

        // gestione del ruolo di admin
        if (email.equals("admin@example.com")) {
            AppUser admin = appUserRepository.findByEmail(email)
                    .orElseThrow(() -> new SecurityException("Credenziali non valide"));


            if (!passwordEncoder.matches(password, admin.getPassword())) {
                throw new SecurityException("Credenziali non valide");
            }

            // forza il ruolo di admin
            admin.setRole(Role.ROLE_ADMIN);
            return jwtTokenUtil.generateToken(admin);
        }

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return jwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            throw new SecurityException("Credenziali non valide", e);
        }
    }

    // Carica un utente dal database tramite email
    public AppUser loadUserByEmail(String email) {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con email: " + email));
    }

    // prende gli psicologi ancora in fase di approvazione
    public List<Psychologist> getPendingPsychologists() {
        return appUserRepository.findAllByRole(Role.ROLE_PSYCHOLOGIST).stream()
                .filter(p -> p instanceof Psychologist && !p.isApprovato())
                .map(p -> (Psychologist) p)
                .toList();
    }

    public Psychologist approvePsychologist(Long psychologistId) {
        // trovo lo psicologo tramite ID
        Psychologist psychologist = (Psychologist) appUserRepository.findById(psychologistId)
                .orElseThrow(() -> new EntityNotFoundException("Psicologo non trovato"));

        // approvo lo psicologo
        psychologist.setApprovato(true);

        // salvo la modifica nel database
        return appUserRepository.save(psychologist);
    }

    public List<Psychologist> searchPsychologists(String keyword) {
        return appUserRepository.searchPsychologists(keyword);
    }
}