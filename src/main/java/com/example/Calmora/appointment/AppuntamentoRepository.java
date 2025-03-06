package com.example.Calmora.appointment;

import com.example.Calmora.psychologist.Psychologist;
import com.example.Calmora.auth.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppuntamentoRepository extends JpaRepository<Appuntamento, Long> {

    // controlla se ci sono altri appuntamenti nelll'orario richiesto
    @Query("SELECT a FROM Appuntamento a WHERE a.psicologo = :psicologo " +
            "AND ((a.dataAppuntamento < :end AND a.dataFineAppuntamento > :start))")
    List<Appuntamento> findConflictingAppointments(
            @Param("psicologo") Psychologist psicologo,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    // trova tutti gli appuntamenti di uno psicologo specifico
    List<Appuntamento> findByPsicologo(Psychologist psicologo);

    // controlla se un determinato psicologo è disponibile in una data e ora
    boolean existsByPsicologoAndDataAppuntamento(Psychologist psicologo, LocalDateTime dataAppuntamento);

    // trova tutti gli appuntamenti di un paziente specifico
    List<Appuntamento> findByPazienteId(Long patientId);

    // controlla se un paziente ha già un appuntamento in un certo intervallo
    @Query("SELECT COUNT(a) > 0 FROM Appuntamento a WHERE a.paziente = :paziente " +
            "AND ((a.dataAppuntamento < :end AND a.dataFineAppuntamento > :start))")
    boolean existsByPatientAndDateRange(
            @Param("paziente") AppUser paziente,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}