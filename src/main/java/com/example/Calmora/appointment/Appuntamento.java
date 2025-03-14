package com.example.Calmora.appointment;

import com.example.Calmora.auth.AppUser;
import com.example.Calmora.psychologist.Psychologist;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
    private Psychologist psicologo;

    @Column(nullable = false)
    private String urlMeet;

    @Column(nullable = false)
    private LocalDateTime dataAppuntamento;

    @Column(nullable = false)
    private LocalDateTime dataFineAppuntamento;

    private boolean confermato;

    public Appuntamento(Psychologist psicologo, AppUser paziente, LocalDateTime dataAppuntamento, LocalDateTime dataFineAppuntamento) {
        this.paziente = paziente;
        this.psicologo = psicologo;
        this.dataAppuntamento = dataAppuntamento;
        this.dataFineAppuntamento = dataFineAppuntamento.plusHours(1);
        this.confermato = true;
        this.urlMeet = psicologo.getUrlMeet();
    }

}
