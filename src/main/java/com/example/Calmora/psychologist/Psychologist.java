package com.example.Calmora.psychologist;

import com.example.Calmora.auth.AppUser;
import com.example.Calmora.auth.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "psychologists")
@NoArgsConstructor
public class Psychologist extends AppUser {

    @Column(nullable = false)
    private String urlCertificato;

    // admin dovrà approvare
    private boolean approvato = false;

    public Psychologist(String name, String surname, String email, String password, String urlCertificato) {
        super(name, surname, email, password, Role.ROLE_PSYCHOLOGIST);

        this.urlCertificato = urlCertificato;
        this.approvato = false;
    }

}
