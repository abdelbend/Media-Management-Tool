package com.example.backend.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entität zur Darstellung der Person-Datenbanktabelle.
 * Speichert Informationen über eine Person, die Medien ausleiht.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "person")
public class PersonEntity {

  /**
   * Primärschlüssel der Person.
   * Wird automatisch generiert und dient zur eindeutigen Identifizierung jeder Person.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long personId;

  /**
   * Der Benutzer, dem die Person gehört. Pflichtfeld
   * Verknüpfung mit der {@link UserEntity}-Tabelle durch einen Foreign Key.
   */
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  /**
   * Vorname der Person. Ein Pflichtfeld.
   */
  @Column(nullable = false)
  private String firstName;

  /**
   * Nachname der Person.Ein Pflichtfeld.
   */
  @Column(nullable = false)
  private String lastName;

  /**
   * Adresse der Person. Ein Pflichtfeld.
   */
  @Column(name = "address", nullable = false)
  private String address;

  /**
   * E-Mail-Adresse der Person. Ein Pflichtfeld.
   */
  @Column(name = "email", nullable = false)
  private String email;

  /**
   * Telefonnummer der Person.
   * Optionales Feld.
   */
  @Column(name = "phone")
  private String phone;

  /**
   * Datum und Uhrzeit der Erstellung der Person.
   * Wird automatisch auf den aktuellen Zeitpunkt gesetzt. Nicht aktualisierbar.
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  /**
   * Verknüpfungen zu den Ausleihen der Person.
   * Beziehung zu {@link LoanEntity} wird ignoriert ({@link JsonIgnore}).
   * Cascade-Einstellungen erlauben das automatische Speichern und Löschen der Verknüpfungen.
   */
  @OneToMany(
            mappedBy = "person",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnore
    private Set<LoanEntity> loans = new HashSet<>();
}
