package com.example.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.backend.dto.MediaCreationDTO;
import com.example.backend.dto.MediaWithCategoriesDTO;
import com.example.backend.model.MediaEntity;
import com.example.backend.model.UserEntity;
import com.example.backend.service.MediaService;
import com.example.backend.service.UserService;

/**
 * REST-Controller zur Verwaltung von Medien.
 * Stellt Möglichkeiten für CRUD-Operationen (Erstellen, Lesen, Aktualisieren, Löschen) zur Verfügung.
 */
@RestController
@RequestMapping("/api/media")
public class MediaController {

  private final MediaService mediaService;

  @Autowired
  private UserService userService;

  /**
   * Konstruktor {@code MediaController}.
   * 
   * @param mediaService Implementierung der Logik für Medien.
   */
  public MediaController(MediaService mediaService) {
    this.mediaService = mediaService;
  }


  /**
   * Gibt alle Medien eines Benutzers mit zugehörigen Kategorien zurück.
   * 
   * @param authentication Authentifizierung des aktuellen Benutzers.
   * @return Eine Liste von {@link MediaWithCategoriesDTO}.
   */
  @GetMapping("/by-username")
  public List<MediaWithCategoriesDTO> getAllMediaByUsernameTest(
    Authentication authentication
  ) {
    String username = authentication.getName();
    return mediaService.getAllMediaByUsernameWithCategories(username);
  }

   /**
   * Erstellt neues Medium und verknüpft es mit einem Benutzer.
   * 
   * @param media Zu erstellenden Mediendaten als {@link MediaCreationDTO}.
   * @param authentication Authentifizierung des aktuellen Benutzers.
   * @return Erstelltes Medium mit Kategorien als {@link MediaWithCategoriesDTO}.
   */
  @PostMapping
  public MediaWithCategoriesDTO createMedia(
    @RequestBody MediaCreationDTO media,
    Authentication authentication
  ) {
    return mediaService.createMedia(media, authentication);
  }

  /**
   * Aktualisiert Informationen eines Mediums.
   * 
   * @param mediaId ID des zu aktualisierenden Mediums.
   * @param dto Neuen Daten als {@link MediaCreationDTO}.
   * @param authentication Authentifizierung des aktuellen Benutzers.
   * @return Das aktualisierte {@link MediaEntity}.
   */
  @PutMapping("/{mediaId}")
  public MediaEntity updateMedia(
    @PathVariable Long mediaId,
    @RequestBody MediaCreationDTO dto,
    Authentication authentication
  ) {
    return mediaService.updateMedia(mediaId, dto, authentication);
  }

  /**
   * Fügt Medium zur Favoritenliste des Benutzers hinzu.
   * 
   * @param mediaId ID des Mediums, das als Favorit markiert werden soll.
   * @param add Informationen des Mediums.
   * @param authentication Authentifizierung des aktuellen Benutzers.
   * @return Aktualisierte Medium mit Kategorien als {@link MediaWithCategoriesDTO}.
   */
  @PutMapping("/{mediaId}/favorite")
  public ResponseEntity<MediaWithCategoriesDTO> addFavorite(
    @PathVariable Long mediaId,
    @RequestBody MediaEntity add,
    Authentication authentication
  ) {
    String username = authentication.getName();
    Optional<UserEntity> userOptional = userService.getUserByUsername(username);

    if (userOptional.isEmpty()) {
      throw new RuntimeException("User not found");
    }

    UserEntity user = userOptional.get();
    add.setUser(user);

    MediaWithCategoriesDTO updatedFavorite = mediaService.addToFavorite(
      mediaId,
      add,
      authentication
    );
    return ResponseEntity.ok(updatedFavorite);
  }

  /**
   * Verknüpft Kategorie mit einem Medium.
   * 
   * @param mediaId ID des Mediums, dem die Kategorie hinzugefügt werden soll.
   * @param categoryId ID der hinzuzufügenden Kategorie.
   * @param authentication Authentifizierung des aktuellen Benutzers.
   * @return Das aktualisierte {@link MediaEntity}.
   */
  @PostMapping("/{mediaId}/assign-category/{categoryId}")
  public MediaEntity assignCategoryToMedia(
    @PathVariable Long mediaId,
    @PathVariable Long categoryId,
    Authentication authentication
  ) {
    return mediaService.assignCategoryToMedia(
      mediaId,
      categoryId,
      authentication
    );
  }

  /**
   * Entfernt Kategorie von einem Medium.
   * 
   * @param mediaId ID des Mediums, von dem die Kategorie entfernt werden soll.
   * @param categoryId ID der zu entfernenden Kategorie.
   * @param authentication Authentifizierung des aktuellen Benutzers.
   */
  @DeleteMapping("/{mediaId}/remove-category/{categoryId}")
  public void removeCategoryFromMedia(
    @PathVariable Long mediaId,
    @PathVariable Long categoryId,
    Authentication authentication
  ) {
    mediaService.removeCategoryFromMedia(mediaId, categoryId, authentication);
  }

  /**
   * Löscht Medium basierend auf seiner ID.
   * 
   * @param id ID des zu löschenden Mediums.
   * @return Eine leere {@link ResponseEntity} mit dem HTTP-Status "204 No Content".
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteMedia(@PathVariable Long id) {
    mediaService.deleteMedia(id);
    return ResponseEntity.noContent().build();
  }
}
