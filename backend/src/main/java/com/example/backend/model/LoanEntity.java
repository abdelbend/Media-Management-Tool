package com.example.backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entität, zur Darstellung der Ausleihe-Datenbanktabelle.
 * Speichert Informationen über Ausleihen von Medien.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loan")
public class LoanEntity {


  /**
   * Primärschlüssel der Ausleihe.
   * Wird automatisch generiert und dient zur eindeutigen Identifizierung jeder Ausleihe.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long loanId;

  /**
   * Verknüpfte Person.
   * Beziehung wird durch Foreign-Key-Verknüpfung mit der Spalte {@code person_id}
   * definiert und darf nicht leer sein ({@code nullable = false}).
   */
  @ManyToOne
  @JoinColumn(name = "person_id", nullable = false)
  private PersonEntity person;

  /**
   * Verknüpftes Medium.
   * Beziehung wird durch Foreign-Key-Verknüpfung mit der Spalte {@code media_id}
   * definiert und darf nicht leer sein ({@code nullable = false}).
   */
  @ManyToOne
  @JoinColumn(name = "media_id", nullable = false)
  private MediaEntity media;

   //Nicht aktualisierbar
  @Column(name = "borrowed_at", nullable = false, updatable = false)
  private LocalDateTime borrowedAt;

  //Optionales Feld wenn Medium zurückgegeben wird.
  @Column(name = "returned_at")
  private LocalDateTime returnedAt;

  //Optionales Feld, wann Medium zurückgegeben werden sollte.
  @Column(name = "due_date")
  private LocalDate dueDate;
}
