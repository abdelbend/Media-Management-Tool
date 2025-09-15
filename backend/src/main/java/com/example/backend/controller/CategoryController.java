package com.example.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.model.CategoryEntity;
import com.example.backend.service.CategoryService;

/**
 * REST-Controller zur Verwaltung von Kategorien. 
 * Stellt Möglichlichkeiten für CRUD-Operationen (Erstellen, Lesen, Aktualisieren, Löschen) zur Verfügung.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

  private final CategoryService categoryService;

  /**
   * Konstruktor  {@code CategoryController}.
   * 
   * @param categoryService Implementiert Logik für Kategorien.
   */
  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  /**
   * Erstellt eine neue Kategorie.
   * 
   * @param categoryDTO Informationen zur neuen Kategorie.
   * @param authentication Die Authentifizierung des aktuellen Benutzers.
   * @return Erstellte {@link CategoryEntity}.
   */
  @PostMapping
  public CategoryEntity createCategory(
    @RequestBody CategoryDTO categoryDTO,
    Authentication authentication
  ) {
    return categoryService.createCategory(categoryDTO, authentication);
  }

  /**
   * Aktualisiert bestehende Kategorie anhand ihrer ID.
   * 
   * @param id Die ID der zur aktualisierenden Kategorie.
   * @param category Aktualisierten Daten der Kategorie.
   * @return Eine {@link ResponseEntity}, die die aktualisierte {@link CategoryEntity} zurückgibt.
   */
  @PutMapping("/{id}")
  public ResponseEntity<CategoryEntity> updateCategory(
    @PathVariable Long id,
    @RequestBody CategoryEntity category
  ) {
    return ResponseEntity.ok(categoryService.updateCategory(id, category));
  }

  /**
   * Löscht Kategorie anhand ihrer ID.
   * 
   * @param id ID der zu löschenden Kategorie.
   * @return Eine leere {@link ResponseEntity} mit dem HTTP-Status "204 No Content".
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.noContent().build();
  }

   /**
   * Gibt Liste alle Kategorien aus, die dem jeweiligen aktuell authentifizierten
   * Benutzer zu geordnet sind. Kategorien liegen als Data Transfer Objects vor.
   * 
   * @param authentication Authentifizierungsinformationen des aktuellen Benutzers.
   * @return Eine Liste von {@link CategoryDTO}-Objekten, die zu dem Benutzer gehören.
   */
  @GetMapping("/user/dto")
  public List<CategoryDTO> getCategoryDTOsForUser(
    Authentication authentication
  ) {
    String username = authentication.getName();
    return categoryService.getCategoryDTOsByUsername(username);
  }
}
