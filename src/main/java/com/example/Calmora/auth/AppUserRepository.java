package com.example.Calmora.auth;

import com.example.Calmora.psychologist.Psychologist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Psychologist> findAllByRole(Role role);

    List<Psychologist> findByRoleAndApprovato(Role role, boolean approvato);

    List<AppUser> findPatientByRole(Role role);

    // cerca psicologi per nome e cognome
    @Query("SELECT p FROM Psychologist p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            "OR LOWER (p.surname) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Psychologist> searchPsychologists(@Param("keyword") String keyword);
}