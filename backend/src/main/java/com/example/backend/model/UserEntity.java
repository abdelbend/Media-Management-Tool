package com.example.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entität zur Darstellung der Benutzer-Datenbanktabelle.
 * Speichert alle Informationen zu einem Benutzer, einschließlich 
 * Anmeldeinformationen wie Benutzername und Passwort.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class UserEntity {


  /**
   * Primärschlüssel des Benutzers.
   * Wird automatisch generiert und dient zur eindeutigen Identifizierung jedes Benutzers.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  /**
   * Benutzername des Benutzers. Benutzername folgt einem Schema und ein Pflichtfeld.
   */
  @Column(nullable = false, unique = true)
  private String username;

  /**
   * Passwort des Benutzers. Pflichtfeld.
   */
  @Column(nullable = false)
  private String password;

  /**
   * E-Mail-Adresse des Benutzers.Email folgt einem Schema und ist Pflicht.
   */
  @Column(nullable = false, unique = true)
  private String email;

  /**
   * Datum und Uhrzeit der Erstellung des Benutzers.
   * Wird automatisch auf den aktuellen Zeitpunkt gesetzt. Nicht aktualisierbar.
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
