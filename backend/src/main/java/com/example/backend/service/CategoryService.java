// package com.example.backend.service;
package com.example.backend.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.model.CategoryEntity;
import com.example.backend.model.UserEntity;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.UserRepository;


/**
 * Verwaltet Kategorien. Enthält Logik zum Erstellen, Aktualisieren, Löschen und Abrufen von Kategorien.
 * Stellt sicher, dass die entsprechenden Benutzer authentifiziert sind und auf ihre eigenen Kategorien zugreifen können.
 */
@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final UserRepository userRepository;

  /**
   * Konstruktor {@link CategoryService}, der die Repositories für Kategorien und Benutzer initialisiert.
   * @param categoryRepository Repository zum Verwalten von Kategorien.
   * @param userRepository Repository zum Verwalten von Benutzern.
   */
  public CategoryService(
    CategoryRepository categoryRepository,
    UserRepository userRepository
  ) {
    this.categoryRepository = categoryRepository;
    this.userRepository = userRepository;
  }

   /**
   * Erzeugt neue Kategorie für den aktuell authentifizierten Benutzer.
   * 
   * @param categoryDTO DTO, welches Dails zu erstellenden Kategorie enthält.
   * @param authentication Authentifizierung des Benutzers, der die Kategorie erstellt.
   * @return Erstellte {@link CategoryEntity}.
   * @throws IllegalArgumentException Wenn der Benutzer nicht gefunden wird.
   */
  public CategoryEntity createCategory(
    CategoryDTO categoryDTO,
    Authentication authentication
  ) {
    String username = authentication.getName();

    UserEntity user = userRepository
      .findByUsername(username)
      .orElseThrow(() ->
        new IllegalArgumentException("User not found: " + username)
      );

    CategoryEntity category = new CategoryEntity();
    category.setCategoryName(categoryDTO.getCategoryName());
    category.setUser(user);
    return categoryRepository.save(category);
  }


  /**
   * Aktualisiert Details einer bestehenden Kategorie.
   * @param categoryId ID der zu aktualisierenden Kategorie.
   * @param categoryDetails Neuen Details der Kategorie.
   * @return Aktualisierte {@link CategoryEntity}.
   * @throws RuntimeException Wenn die Kategorie nicht gefunden wird.
   */
  public CategoryEntity updateCategory(
    Long categoryId,
    CategoryEntity categoryDetails
  ) {
    CategoryEntity category = categoryRepository
      .findById(categoryId)
      .orElseThrow(() -> new RuntimeException("Category not found"));
    category.setCategoryName(categoryDetails.getCategoryName());
    return categoryRepository.save(category);
  }

  /**
   * Löscht Kategorie anhand ihrer ID.
   * @param categoryId ID der zu löschenden Kategorie.
   */
  public void deleteCategory(Long categoryId) {
    categoryRepository.deleteById(categoryId);
  }

  /**
   * Ruft Liste von {@link CategoryDTO}s für einen bestimmten Benutzer ab.
   * @param username Benutzername des Benutzers, dessen Kategorien abgerufen werden.
   * @return Liste von {@link CategoryDTO}s.
   */
  public List<CategoryDTO> getCategoryDTOsByUsername(String username) {
    return categoryRepository.findCategoryDTOsByUsername(username);
  }
}
