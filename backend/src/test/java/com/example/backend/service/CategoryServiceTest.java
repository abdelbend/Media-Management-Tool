package com.example.backend.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.model.CategoryEntity;
import com.example.backend.model.UserEntity;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.UserRepository;

class CategoryServiceTest {

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private Authentication authentication;

  @InjectMocks
  private CategoryService categoryService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }
/**
 * Testet das erfolgreiche Erstellen einer Kategorie.
 * Zu erwarten: Kategorie korrekt erstellt und die richtigen Werte zurückgegeben.
 */
  @Test
  void testCreateCategory_Success() {
    CategoryDTO categoryDTO = new CategoryDTO();
    categoryDTO.setCategoryName("TestCategory");

    UserEntity userEntity = new UserEntity();
    userEntity.setUsername("testUser");

    when(authentication.getName()).thenReturn("testUser");
    when(userRepository.findByUsername("testUser"))
      .thenReturn(Optional.of(userEntity));

    CategoryEntity savedEntity = new CategoryEntity();
    savedEntity.setCategoryId(1L);
    savedEntity.setCategoryName("TestCategory");
    savedEntity.setUser(userEntity);

    when(categoryRepository.save(any(CategoryEntity.class)))
      .thenReturn(savedEntity);

    CategoryEntity result = categoryService.createCategory(
      categoryDTO,
      authentication
    );

    assertNotNull(result);
    assertEquals("TestCategory", result.getCategoryName());
    verify(categoryRepository, times(1)).save(any(CategoryEntity.class));
  }


/**
 * Testet das Erstellen einer Kategorie, wenn der Benutzer nicht gefunden wird.
 * Zu erwarten: IllegalArgumentException geworfen, wenn der Benutzer nicht existiert.
 */
  @Test
  void testCreateCategory_UserNotFound_ShouldThrowException() {
    CategoryDTO categoryDTO = new CategoryDTO();
    categoryDTO.setCategoryName("TestCategory");

    when(authentication.getName()).thenReturn("nonExistingUser");
    when(userRepository.findByUsername("nonExistingUser"))
      .thenReturn(Optional.empty());

    assertThrows(
      IllegalArgumentException.class,
      () -> categoryService.createCategory(categoryDTO, authentication)
    );
  }

  /**
 * Testet das erfolgreiche Aktualisieren einer Kategorie.
 * Zu erwarten: Name der Kategorie erfolgreich aktualisiert.
 */
  @Test
  void testUpdateCategory_Success() {
    Long categoryId = 1L;
    CategoryEntity existingCategory = new CategoryEntity();
    existingCategory.setCategoryId(categoryId);
    existingCategory.setCategoryName("OldName");

    CategoryEntity updatedDetails = new CategoryEntity();
    updatedDetails.setCategoryName("NewName");

    when(categoryRepository.findById(categoryId))
      .thenReturn(Optional.of(existingCategory));
    when(categoryRepository.save(any(CategoryEntity.class)))
      .thenReturn(existingCategory);

    CategoryEntity result = categoryService.updateCategory(
      categoryId,
      updatedDetails
    );

    assertEquals("NewName", result.getCategoryName());
    verify(categoryRepository, times(1)).findById(categoryId);
    verify(categoryRepository, times(1)).save(existingCategory);
  }

  /**
 * Testet das Aktualisieren einer Kategorie, wenn die Kategorie nicht gefunden wird.
 * Zu erwarten: RuntimeException geworfen, wenn die Kategorie nicht existiert.
 */
  @Test
  void testUpdateCategory_NotFound_ShouldThrowRuntimeException() {
    Long categoryId = 1L;
    CategoryEntity updatedDetails = new CategoryEntity();
    updatedDetails.setCategoryName("DoesNotMatter");

    when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

    assertThrows(
      RuntimeException.class,
      () -> categoryService.updateCategory(categoryId, updatedDetails)
    );
  }


/**
 * Testet das erfolgreiche Löschen einer Kategorie.
 * Zu erwarten: Kategorie ohne Fehler gelöscht.
 */
  @Test
  void testDeleteCategory_Success() {
    Long categoryId = 1L;

    doNothing().when(categoryRepository).deleteById(categoryId);

    categoryService.deleteCategory(categoryId);

    verify(categoryRepository, times(1)).deleteById(categoryId);
  }

  /**
 * Testet das Abrufen von Kategorie-Datenübertragungsobjekten (DTOs) anhand des Benutzernamens.
 * Zu erwarten: Kategorie-Datenübertragungsobjekte korrekt zurückgegeben.
 */
  @Test
  void testGetCategoryDTOsByUsername_Success() {
    String username = "testUser";
    CategoryDTO catDto1 = new CategoryDTO(1L, "Cat1");
    CategoryDTO catDto2 = new CategoryDTO(2L, "Cat2");
    when(categoryRepository.findCategoryDTOsByUsername(username))
      .thenReturn(List.of(catDto1, catDto2));

    List<CategoryDTO> result = categoryService.getCategoryDTOsByUsername(
      username
    );

    assertEquals(2, result.size());
    assertEquals("Cat1", result.get(0).getCategoryName());
    assertEquals("Cat2", result.get(1).getCategoryName());
  }
}
