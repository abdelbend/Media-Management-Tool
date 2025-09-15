package com.example.backend.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entität zur Darstellung der Medien-Datenbanktabelle.
 * Speichert alle Informationen zu einem Medium und die Verknüpfungen zu Kategorien und Ausleihen.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "media")
public class MediaEntity {

  /**
   * Primärschlüssel des Mediums.
   * Wird automatisch generiert und dient zur eindeutigen Identifizierung jedes Mediums.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long mediaId;

  /**
   * Der Benutzer, der das Medium erstellt hat.
   * Verknüpfung mit der {@link UserEntity}-Tabelle durch einen Foreign Key.
   */
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  /**
   * Produzent oder Hersteller des Mediums.
   * Optionales Feld.
   */
  @Column(name = "producer")
  private String producer;

  // Titel des Mediums. Ein Pflichtfeld
  @Column(nullable = false)
  private String title;

  /**Zustand vom Medium. Ein Pflichtfeld. 
  *Wird als {@link EnumType#STRING} gespeichert.
  */
  @Enumerated(EnumType.STRING)
  @Column(name = "media_state", nullable = false)
  private MediaState mediaState;

  /**
   * Typ des Mediums.
   * Wird als {@link EnumType#STRING} gespeichert. Pflichtfeld.
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MediaType type;

  @Column(name = "release_year")
  private Integer releaseYear;

  /**
   * Notizen zu dem Medium.
   * Wird in der Datenbank als "Large Object" gespeichert ({@link Lob}).
   * Optionales Feld.
   */
  @Lob
  @Column(name = "notes")
  private String notes; // indicates that the property should be stored in the database in the form of a large object type in the database.

  @Column(name = "isbn")
  private String isbn;

  /**
   * Gibt an, ob das Medium als Favorit markiert ist.
   * Standardwert: {@code false}.
   */
  @Column(name = "is_favorite", nullable = false)
  private Boolean isFavorite = false;

  /**
   * Datum und Uhrzeit der Erstellung des Mediums.
   * Wird automatisch auf den aktuellen Zeitpunkt gesetzt. Nicht aktualisierbar.
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  /**
   * Verknüpfungen zu den Kategorien des Mediums.
   * Beziehung zu {@link MediaCategory} wird ignoriert ({@link JsonIgnore}).
   * Cascade-Einstellungen erlauben das automatische Speichern und Löschen der Verknüpfungen.
   */
  @OneToMany(
    mappedBy = "media",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  @JsonIgnore
  private Set<MediaCategory> mediaCategories = new HashSet<>();

  /**
   * Verknüpfungen zu den Ausleihen des Mediums.
   * Beziehungs zu {@link LoanEntity} wird ignoriert ({@link JsonIgnore}).
   * Cascade-Einstellungen erlauben das automatische Speichern und Löschen der Verknüpfungen.
   */
  @OneToMany(
    mappedBy = "media",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  @JsonIgnore
  private Set<LoanEntity> loans = new HashSet<>();

  /**
   * Liefert die Kategorien des Mediums direkt.
   * Methode erstellt eine Menge von {@link CategoryEntity}, indem sie 
   * die Verknüpfungen in {@code mediaCategories} durchläuft.
   * 
   * @return Menge von {@link CategoryEntity}, die mit diesem Medium verknüpft sind.
   */
  @Transient
  public Set<CategoryEntity> getCategories() {
    Set<CategoryEntity> categories = new HashSet<>();
    for (MediaCategory mediaCategory : mediaCategories) {
      categories.add(mediaCategory.getCategory());
    }
    return categories;
  }


}
