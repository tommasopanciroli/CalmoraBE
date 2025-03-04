package com.example.Calmora.auth;

import com.example.Calmora.psychologist.Psychologist;
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
import java.util.Set;
import java.util.stream.Collectors;

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
            user = new Psychologist(name, surname, email, encodedPassword, urlCertificato);
        } else {
            user = new AppUser(name, surname, email, encodedPassword, role);
        }

        return appUserRepository.save(user);
    }

    // Cerca un utente tramite email
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