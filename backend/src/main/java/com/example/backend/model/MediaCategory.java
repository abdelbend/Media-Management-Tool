package com.example.backend.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
 * Entität zur Darstellung der Verknüpfung zwischen Medien und Kategorien.
 * Diese Tabelle ordnet Medien einer oder mehreren Kategorien zu.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "media_category")
public class MediaCategory {

  /**
   * Primärschlüssel der Media-Category-Verknüpfung.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Das mit der Kategorie verknüpfte Medium.
   * Beziehung wird durch eine Foreign-Key-Verknüpfung mit der Spalte {@code media_id}
   * definiert. Ein Pflichtfeld.
   * 
   * Die Verknüpfung wird von der Serialisierung ignoriert.
   * ({@link JsonIgnore}).
   */
  @ManyToOne
  @JoinColumn(name = "media_id", nullable = false)
  @JsonIgnore
  private MediaEntity media;

  /**
   * Die mit dem Medium verknüpfte Kategorie.
   * Beziehung wird durch eine Foreign-Key-Verknüpfung mit der Spalte {@code category_id}
   * definiert. Ein Pflichtfeld.
   * 
   * Die Verknüpfung wird von der Serialisierung ignoriert .
   * ({@link JsonIgnore}).
   */
  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  @JsonIgnore
  private CategoryEntity category;

  /**
   * Datum und Uhrzeit der Erstellung der Verknüpfung.
   * Dieses Feld wird automatisch auf den aktuellen Zeitpunkt gesetzt. 
   * Nicht aktualisierbar ({@code updatable = false}).
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
