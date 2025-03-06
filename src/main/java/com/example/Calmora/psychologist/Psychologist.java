package com.example.Calmora.psychologist;

import com.example.Calmora.auth.AppUser;
import com.example.Calmora.google.CalendarService;
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

    // admin dovrà approvare
    private boolean approvato = false;

    public Psychologist(String name, String surname, String email, String password, String urlCertificato, String urlMeet) {
        super(name, surname, email, password, Role.ROLE_PSYCHOLOGIST);

        this.urlCertificato = urlCertificato;
        this.approvato = false;
        this.urlMeet = urlMeet;
    }

}
