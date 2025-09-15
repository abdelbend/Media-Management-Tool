package com.example.backend.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import com.example.backend.dto.MediaCreationDTO;
import com.example.backend.dto.MediaWithCategoriesDTO;
import com.example.backend.dto.MediaWithCategoriesProjection;
import com.example.backend.model.CategoryEntity;
import com.example.backend.model.MediaCategory;
import com.example.backend.model.MediaEntity;
import com.example.backend.model.MediaType;
import com.example.backend.model.UserEntity;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.MediaCategoryRepository;
import com.example.backend.repository.MediaRepository;

class MediaServiceTest {

  @Mock
  private MediaRepository mediaRepository;

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private MediaCategoryRepository mediaCategoryRepository;

  @Mock
  private UserService userService;

  @Mock
  private Authentication authentication;

  @InjectMocks
  private MediaService mediaService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * Testet den Erfolg des Abrufens aller Medien mit Kategorien für einen bestimmten Benutzer.
   * Überprüft, dass die Medien korrekt abgerufen werden, wenn der Benutzer vorhanden ist.
   */
  @Test
  void testGetAllMediaByUsernameWithCategories_Success() {
    String username = "testUser";
    UserEntity mockUser = new UserEntity();
    mockUser.setUserId(123L);
    mockUser.setUsername(username);

    when(userService.getUserByUsername(username))
      .thenReturn(Optional.of(mockUser));

    MediaWithCategoriesProjection projection = mock(
      MediaWithCategoriesProjection.class
    );
    when(projection.getMediaId()).thenReturn(1L);
    when(mediaRepository.findMediaWithCategoriesByUserId(123L))
      .thenReturn(List.of(projection));

    List<MediaWithCategoriesDTO> result = mediaService.getAllMediaByUsernameWithCategories(
      username
    );

    assertEquals(1, result.size());
    verify(mediaRepository, times(1)).findMediaWithCategoriesByUserId(123L);
  }



  /**
   * Testet die erfolgreiche Erstellung eines neuen Mediums.
   * Überprüft, ob das Medium korrekt erstellt wird und die zugehörigen Kategorien gesetzt sind.
   */
  @Test
  void testCreateMedia_Success() {
    String username = "testUser";
    UserEntity mockUser = new UserEntity();
    mockUser.setUserId(1L);
    mockUser.setUsername(username);

    when(authentication.getName()).thenReturn(username);
    when(userService.getUserByUsername(username))
      .thenReturn(Optional.of(mockUser));

    MediaCreationDTO dto = new MediaCreationDTO();
    dto.setProducer("Producer");
    dto.setTitle("Title");
    dto.setMediaState("AVAILABLE");
    dto.setType("BOOK");
    dto.setReleaseYear(2021);
    dto.setNotes("Some notes");
    dto.setIsbn("123-ISBN");
    dto.setIsFavorite(true);
    dto.setCategories(List.of(100L));

    CategoryEntity categoryEntity = new CategoryEntity();
    categoryEntity.setCategoryId(100L);
    categoryEntity.setCategoryName("TestCategory");

    when(categoryRepository.findById(100L))
      .thenReturn(Optional.of(categoryEntity));
    when(mediaRepository.save(any(MediaEntity.class)))
      .thenAnswer(invocation -> {
        MediaEntity saved = invocation.getArgument(0);
        saved.setMediaId(999L);
        return saved;
      });

    MediaWithCategoriesDTO result = mediaService.createMedia(
      dto,
      authentication
    );

    assertNotNull(result.getMediaId());
    assertEquals("Title", result.getTitle());
    assertEquals(1, result.getCategories().size());
    verify(mediaRepository, times(1)).save(any(MediaEntity.class));
  }

  /**
   * Testet das erfolgreiche Update eines Mediums.
   * Überprüft, dass die Änderungen am Medium korrekt gespeichert werden.
   */
  @Test
  void testUpdateMedia_Success() {
    String username = "testUser";
    UserEntity mockUser = new UserEntity();
    mockUser.setUserId(1L);

    MediaEntity existingMedia = new MediaEntity();
    existingMedia.setMediaId(10L);
    existingMedia.setUser(mockUser);

    MediaCreationDTO dto = new MediaCreationDTO();
    dto.setProducer("NewProducer");
    dto.setTitle("NewTitle");
    dto.setMediaState("AVAILABLE");
    dto.setType("BOOK");
    dto.setIsFavorite(true);

    when(authentication.getName()).thenReturn(username);
    when(userService.getUserByUsername(username))
      .thenReturn(Optional.of(mockUser));
    when(mediaRepository.findById(10L)).thenReturn(Optional.of(existingMedia));
    when(mediaRepository.save(any(MediaEntity.class)))
      .thenReturn(existingMedia);

    MediaEntity result = mediaService.updateMedia(10L, dto, authentication);

    assertEquals("NewProducer", result.getProducer());
    assertEquals("NewTitle", result.getTitle());
    assertEquals(MediaType.BOOK, result.getType());
    assertTrue(result.getIsFavorite());
    verify(mediaRepository, times(1)).save(existingMedia);
  }

  /**
   * Testet das erfolgreiche Zuordnen einer Kategorie zu einem Medium.
   * Überprüft, dass das Medium korrekt mit der Kategorie verknüpft wird.
   */
  @Test
  void testAssignCategoryToMedia_Success() {
    String username = "testUser";
    when(authentication.getName()).thenReturn(username);

    UserEntity user = new UserEntity();
    user.setUsername(username);

    MediaEntity media = new MediaEntity();
    media.setMediaId(1L);
    media.setUser(user);

    CategoryEntity category = new CategoryEntity();
    category.setCategoryId(2L);
    category.setUser(user);

    when(mediaRepository.findById(1L)).thenReturn(Optional.of(media));
    when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
    when(
      mediaCategoryRepository.existsByMediaMediaIdAndCategoryCategoryId(1L, 2L)
    )
      .thenReturn(false);

    MediaEntity result = mediaService.assignCategoryToMedia(
      1L,
      2L,
      authentication
    );

    assertEquals(1L, result.getMediaId());
    verify(mediaCategoryRepository, times(1)).save(any(MediaCategory.class));
  }

  /**
   * Testet das erfolgreiche Entfernen einer Kategorie von einem Medium.
   * Überprüft, dass die Kategorie korrekt aus dem Medium entfernt wird.
   */
  @Test
  void testRemoveCategoryFromMedia_Success() {
    String username = "testUser";
    when(authentication.getName()).thenReturn(username);

    UserEntity user = new UserEntity();
    user.setUsername(username);

    MediaEntity media = new MediaEntity();
    media.setMediaId(1L);
    media.setUser(user);

    CategoryEntity category = new CategoryEntity();
    category.setCategoryId(2L);
    category.setUser(user);

    MediaCategory mediaCategory = new MediaCategory();
    mediaCategory.setMedia(media);
    mediaCategory.setCategory(category);

    when(mediaRepository.findById(1L)).thenReturn(Optional.of(media));
    when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
    when(
      mediaCategoryRepository.findByMediaMediaIdAndCategoryCategoryId(1L, 2L)
    )
      .thenReturn(Optional.of(mediaCategory));

    mediaService.removeCategoryFromMedia(1L, 2L, authentication);

    verify(mediaCategoryRepository, times(1)).delete(mediaCategory);
  }

  /**
   * Testet das erfolgreiche Löschen eines Mediums.
   * Überprüft, dass das Medium aus der Datenbank gelöscht wird.
   */
  @Test
  void testDeleteMedia_Success() {
    Long mediaId = 999L;
    doNothing().when(mediaRepository).deleteById(mediaId);

    mediaService.deleteMedia(mediaId);

    verify(mediaRepository, times(1)).deleteById(mediaId);
  }
}
