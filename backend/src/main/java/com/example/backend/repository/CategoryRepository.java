package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.model.CategoryEntity;

/**
 * Repository zur Verwaltung der {@link CategoryEntity}-Daten.
 * Ermöglicht Zugriff auf {@link CategoryEntity}-Datenbanktabelle und stellt
 * Methoden zum Abrufen von Kategorien anhand verschiedenen Kriterien zur Verfügung.
 */
@Repository
public interface CategoryRepository
  extends JpaRepository<CategoryEntity, Long> {
    /**
   * Findet Liste von Kategorien anhand des Kategorie-Namens.
   * 
   * @param categoryName Name der Kategorie, nach dem gesucht wird.
   * @return Eine Liste von {@link CategoryEntity}-Objekten, die den angegebenen
   *         Kategorie-Namen haben.
   */
  List<CategoryEntity> findByCategoryName(String categoryName);

  /**
   * Findet Liste von {@link CategoryDTO} anhand des Benutzernamen des
   * jeweiligen Benutzers.
   * 
   * @param username Der Benutzername des Benutzers, dessen Kategorien abgerufen werden sollen.
   * @return Eine Liste von {@link CategoryDTO}-Objekten, die die Kategorie-ID und den 
   * Kategorie-Namen enthalten und zu dem jeweiligen Benutzernamen gehören.
   */
  @Query(
    "SELECT new com.example.backend.dto.CategoryDTO(c.categoryId, c.categoryName) " +
    "FROM CategoryEntity c " +
    "WHERE c.user.username = :username"
  )
  List<CategoryDTO> findCategoryDTOsByUsername(String username);
}
