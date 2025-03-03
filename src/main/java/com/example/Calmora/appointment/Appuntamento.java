package com.example.Calmora.appointment;

import com.example.Calmora.auth.AppUser;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Table(name = "appuntamenti")
public class Appuntamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paziente_id")
    private AppUser paziente;

    @ManyToOne
    @JoinColumn(name = "psicologo_id")
    private AppUser psicologo;

    private LocalDateTime dataAppuntamento;

    private boolean confermato;

    public Appuntamento(AppUser paziente, AppUser psicologo, LocalDateTime dataAppuntamento) {
        this.paziente = paziente;
        this.psicologo = psicologo;
        this.dataAppuntamento = dataAppuntamento;
        this.confermato = true;
    }

}
