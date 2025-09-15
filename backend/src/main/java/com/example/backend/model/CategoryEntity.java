package com.example.backend.model;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entität zur Darstellung der Kategorie-Datenbanktabelle.
 * Verwendet, um Datenbankoperationen für Kategorien zu ermöglichen. 
 * Enthält Informationen über die Kategorie-ID, den zugehörigen Benutzer,
 * den Namen der Kategorie und das Erstellungsdatum.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "category")
public class CategoryEntity {

  /**
   * Eindeutige ID der Kategorie.
   * Spalte wird automatisch generiert, durch Verwendung der {@link GenerationType#IDENTITY} Strategie.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long categoryId;

  /**
   * Verknüpfter Benutzer.
   * Beziehung wird durch Foreign-Key-Verknüpfung mit der Spalte {@code user_id}
   * definiert und darf nicht leer sein ({@code nullable = false}).
   */
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  // Erforderliche Spalte.
  @Column(nullable = false)
  private String categoryName;

   /**
   * Erstellungsdatum der Kategorie.
   * Spalte wird beim Erstellen der Kategorie automatisch auf den aktuellen Zeitpunkt
   * gesetzt. Nicht aktualisierbar ({@code updatable = false}).
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

   @OneToMany(
          mappedBy = "category",
          cascade = CascadeType.ALL,
          orphanRemoval = true
  )
  @JsonIgnore
  private Set<MediaCategory> mediaCategories = new HashSet<>();

}
