package com.example.backend.dto;

import java.time.LocalDateTime;

/**
 * Ein Interface, das die Projektion von Medien mit zugehörigen Kategorien beschreibt.
 * Wird verwendet, um Daten aus der Datenbank abzurufen, ohne, dass die vollständigen Entitäten geladen werden müssen.
 */
public interface MediaWithCategoriesProjection {
  Long getMediaId();
  Long getUserId();
  String getProducer();
  String getTitle();
  String getMediaState();
  String getType();
  Integer getReleaseYear();
  String getNotes();
  String getIsbn();
  Boolean getIsFavorite();
  LocalDateTime getCreatedAt();

  /**
   * Gibt Zeichenkette zurück, die die Kategorie-IDs und -Namen des Mediums enthält.
   * @return Kategorie-Paare als Zeichenkette.
   */
  String getCategoryPairs();
}
