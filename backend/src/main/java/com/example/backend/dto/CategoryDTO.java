package com.example.backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object; repräsentiert eine Kategorie.
 * Verwendet, um Daten über Kategorien zwischen verschiedenen
 * Schichten der Anwendung zu übertragen.
 */
@Setter
@Getter
@Data
public class CategoryDTO {

  private Long categoryId;
  private String categoryName;

  /**
   * Standard-Konstruktor zur Erstellung eines leeren {@code CategoryDTO}-Objekts.
   */
  public CategoryDTO() {}

  /**
   * Konstruktor, der ein {@code CategoryDTO}-Objekt mit der angegebenen ID und dem Namen initialisiert.
   *
   * @param categoryId Eindeutige ID der Kategorie.
   * @param categoryName Name der Kategorie.
   */
  public CategoryDTO(Long categoryId, String categoryName) {
    this.categoryId = categoryId;
    this.categoryName = categoryName;
  }
}
