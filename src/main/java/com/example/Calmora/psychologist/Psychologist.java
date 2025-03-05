package com.example.Calmora.psychologist;

import com.example.Calmora.auth.AppUser;
import com.example.Calmora.role.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "psychologists")
@NoArgsConstructor
@Getter
@Setter
public class Psychologist extends AppUser {

    @Column(nullable = false)
    private String urlCertificato;

    @Column(nullable = false, unique = true)
    private String urlMeet;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private AppUser appUser;

    // admin dovrà approvare
    @Getter
    private boolean approvato = false;

    public Psychologist(String name, String surname, String email, String password, String urlCertificato) {
        super(name, surname, email, password, Role.ROLE_PSYCHOLOGIST);

        this.urlCertificato = urlCertificato;
        this.approvato = false;
        this.urlMeet = generateRandomUrlMeet();
    }

    // genera un link meet per ogni psicologo
    private String generateRandomUrlMeet() {
        String random = java.util.UUID.randomUUID().toString().substring(0, 8);
        return "https://meet.google.com/" + random;
    }
}
