package com.example.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.dto.MediaCreationDTO;
import com.example.backend.dto.MediaWithCategoriesDTO;
import com.example.backend.dto.MediaWithCategoriesProjection;
import com.example.backend.model.CategoryEntity;
import com.example.backend.model.MediaCategory;
import com.example.backend.model.MediaEntity;
import com.example.backend.model.MediaState;
import com.example.backend.model.MediaType;
import com.example.backend.model.UserEntity;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.MediaCategoryRepository;
import com.example.backend.repository.MediaRepository;

@Service
public class MediaService {

  private final MediaRepository mediaRepository;
  private final CategoryRepository categoryRepository;
  private final MediaCategoryRepository mediaCategoryRepository;

  @Autowired
  private final UserService userService;

  /**
   * Konstruktor, um Abhängigkeiten des MediaService zu initialisieren.
   * @param mediaRepository Repository für Medien-Entitäten
   * @param userService Service für Benutzer-bezogene Operationen
   * @param categoryRepository Repository für Kategorie-Entitäten
   * @param mediaCategoryRepository Repository für Medien-Kategorie-Verknüpfungen
   */
  public MediaService(
    MediaRepository mediaRepository,
    UserService userService,
    CategoryRepository categoryRepository,
    MediaCategoryRepository mediaCategoryRepository
  ) {
    this.userService = userService;
    this.mediaRepository = mediaRepository;
    this.categoryRepository = categoryRepository;
    this.mediaCategoryRepository = mediaCategoryRepository;
  }

  /**
   * Ruft Medien eines Benutzers anhand des Benutzernamens ab mit der zugehörigen Kategorien.
   * @param username Benutzername des Benutzers, dessen Medien abgerufen werden sollen.
   * @return Liste von MediaWithCategoriesDTO, die Medien mit den zugehörigen Kategorien darstellen.
   */
  public List<MediaWithCategoriesDTO> getAllMediaByUsernameWithCategories(
    String username
  ) {
    UserEntity user = userService
      .getUserByUsername(username)
      .orElseThrow(() -> new RuntimeException("User not found"));

    Long userId = user.getUserId();

    // Using 'categoryPairs' projection to aggregate categories
    List<MediaWithCategoriesProjection> rawResults = mediaRepository.findMediaWithCategoriesByUserId(
      userId
    );

    List<MediaWithCategoriesDTO> dtos = new ArrayList<>();
    for (MediaWithCategoriesProjection row : rawResults) {
      dtos.add(MediaWithCategoriesDTO.fromProjection(row));
    }

    return dtos;
  }

  /**
   * Ruft alle Medien ab.
   * @return Liste aller Medien
   */
  public List<MediaEntity> getAllMedia() {
    return mediaRepository.findAll();
  }

    /**
   * Ruft Medien eines Benutzers anhand seines Benutzernamens ab.
   * @param username Benutzername des Benutzers, dessen Medien abgerufen werden sollen.
   * @return Liste von Medien, die diesem Benutzer gehören.
   */
  public List<MediaEntity> getAllMediaByUsername(String username) {
    Optional<UserEntity> userOptional = userService.getUserByUsername(username);
    if (userOptional.isEmpty()) {
      throw new RuntimeException("User not found");
    }
    UserEntity user = userOptional.get();
    return mediaRepository.findByUserUserId(user.getUserId());
  }

  /**
   * Ruft Medienentität anhand ihrer ID ab.
   * @param mediaId ID des abzurufenden Mediums
   * @return Optional mit der Medienentität, falls gefunden, sonst leer.
   */
  public Optional<MediaEntity> getMediaById(Long mediaId) {
    return mediaRepository.findById(mediaId);
  }

  /**
   * Ruft alle Medien eines Benutzers anhand seiner Benutzer-ID ab.
   * @param userId ID des Benutzers, dessen Medien abgerufen werden sollen.
   * @return Liste von Medien, die diesem Benutzer gehören.
   */
  public List<MediaEntity> getMediaByUserId(Long userId) {
    return mediaRepository.findByUserUserId(userId);
  }



    /**
   * Setzt Medium als Favorit für den aktuellen Benutzer.
   * @param mediaId ID des Mediums, das als Favorit markiert werden soll.
   * @param modifyMedia Medium, das die zu ändernde Favoriten-Eigenschaft enthält.
   * @param authentication  Authentifizierungsinformationen des aktuellen Benutzers.
   * @return DTO des aktualisierten Mediums mit den zugehörigen Kategorien.
   */
  public MediaWithCategoriesDTO addToFavorite(
    Long mediaId,
    MediaEntity modifyMedia,
    Authentication authentication
  ) {
    String username = authentication.getName();
    Optional<UserEntity> userOptional = userService.getUserByUsername(username);

    if (userOptional.isEmpty()) {
      throw new RuntimeException("User not found");
    }

    UserEntity user = userOptional.get();

    MediaEntity currentMedia = mediaRepository
      .findById(mediaId)
      .orElseThrow(() -> new RuntimeException("Media not found"));

    // Überprüfen, ob das Medium dem aktuellen Benutzer gehört (optional)
    if (!currentMedia.getUser().getUserId().equals(user.getUserId())) {
      throw new RuntimeException(
        "You don't have permission to modify this media."
      );
    }

    // Setzt den Favoritenstatus basierend auf dem eingehenden 'modifyMedia'
    currentMedia.setIsFavorite(modifyMedia.getIsFavorite());

    currentMedia = mediaRepository.save(currentMedia);

    // Bauen des MediaWithCategoriesDTO aus dem 'currentMedia'
    MediaWithCategoriesDTO dto = new MediaWithCategoriesDTO();
    dto.setMediaId(currentMedia.getMediaId());
    dto.setUserId(user.getUserId());
    dto.setProducer(currentMedia.getProducer());
    dto.setTitle(currentMedia.getTitle());
    dto.setMediaState(currentMedia.getMediaState().name());
    dto.setType(currentMedia.getType().name());
    dto.setReleaseYear(currentMedia.getReleaseYear());
    dto.setNotes(currentMedia.getNotes());
    dto.setIsbn(currentMedia.getIsbn());
    dto.setIsFavorite(currentMedia.getIsFavorite());
    dto.setCreatedAt(currentMedia.getCreatedAt());

    // Konvertieren von jeder verknüpften MediaCategory -> CategoryDTO
    if (currentMedia.getMediaCategories() != null) {
      currentMedia
        .getMediaCategories()
        .forEach(mc -> {
          CategoryEntity cat = mc.getCategory();
          if (cat != null) {
            CategoryDTO catDTO = new CategoryDTO();
            catDTO.setCategoryId(cat.getCategoryId());
            catDTO.setCategoryName(cat.getCategoryName());
            dto.getCategories().add(catDTO);
          }
        });
    }

    return dto;
  }

    /**
   * Ruft Medien eines bestimmten Medienstatus ab.
   * @param mediaState Medienstatus, nach dem gefiltert werden soll.
   * @return Liste von Medien mit dem angegebenen Status
   */
  public List<MediaEntity> getMediaByMediaState(MediaState mediaState) {
    return mediaRepository.findByMediaState(mediaState);
  }

    /**
   * Ruft Medien eines bestimmten Typs ab.
   * @param type Medientyp, nach dem gefiltert werden soll.
   * @return Liste von Medien des angegebenen Typs.
   */
  public List<MediaEntity> getMediaByType(MediaType type) {
    return mediaRepository.findByType(type);
  }


  /**
   * @return Eine Liste von Medien, die als Favoriten markiert wurden.
   */
  public List<MediaEntity> getFavoriteMedia() {
    return mediaRepository.findByIsFavorite(true);
  }

   /**
   * @param isbn  ISBN des Mediums
   * @return Optional mit der Medienentität, wenn gefunden, sonst leer.
   */
  public Optional<MediaEntity> getMediaByIsbn(String isbn) {
    return mediaRepository.findByIsbn(isbn);
  }

   /**
   * Erstellt neues Medium für den aktuellen Benutzer.
   * @param media Medientypen und Informationen zur Erstellung eines neuen Mediums.
   * @param authentication Authentifizierungsinformationen des Benutzers.
   * @return Erstelltes Medium als DTO.
   */
  public MediaWithCategoriesDTO createMedia(
    MediaCreationDTO media,
    Authentication authentication
  ) {
    String username = authentication.getName();
    UserEntity user = userService
      .getUserByUsername(username)
      .orElseThrow(() -> new RuntimeException("User not found"));

    MediaEntity mediaEntity = new MediaEntity();
    mediaEntity.setUser(user);
    mediaEntity.setProducer(media.getProducer());
    mediaEntity.setTitle(media.getTitle());
    mediaEntity.setMediaState(MediaState.valueOf(media.getMediaState()));
    mediaEntity.setType(MediaType.valueOf(media.getType()));
    mediaEntity.setReleaseYear(media.getReleaseYear());
    mediaEntity.setNotes(media.getNotes());
    mediaEntity.setIsbn(media.getIsbn());
    mediaEntity.setIsFavorite(media.getIsFavorite());
    mediaEntity.setCreatedAt(LocalDateTime.now());

    // Bauen vom DTO aus MediaCreationDTO
    MediaWithCategoriesDTO mediaWithCategoriesDTO = new MediaWithCategoriesDTO(
      media
    );

    // Für jede Kategoriewahl in der Anfrage, finden wir die Kategorie
    for (Long categoryId : media.getCategories()) {
      CategoryEntity categoryEntity = categoryRepository
        .findById(categoryId)
        .orElseThrow(() -> new RuntimeException("Category not found"));

      MediaCategory mediaCategory = new MediaCategory();
      mediaCategory.setMedia(mediaEntity);
      mediaCategory.setCategory(categoryEntity);
      mediaEntity.getMediaCategories().add(mediaCategory);

      // Auch eine CategoryDTO zum Rückgabewert hinzufügen
      CategoryDTO catDTO = new CategoryDTO();
      catDTO.setCategoryId(categoryEntity.getCategoryId());
      catDTO.setCategoryName(categoryEntity.getCategoryName());
      mediaWithCategoriesDTO.getCategories().add(catDTO);
    }

    mediaRepository.save(mediaEntity);

    mediaWithCategoriesDTO.setMediaId(mediaEntity.getMediaId());
    mediaWithCategoriesDTO.setUserId(user.getUserId());

    return mediaWithCategoriesDTO;
  }

    /**
   * Aktualisiert bestehendes Medium mit den neuen Daten.
   *
   * @param mediaId ID des zu aktualisierenden Mediums.
   * @param currentMedia DTO mit neuen Medieninformationen.
   * @param authentication Authentifizierungsinformationen des aktuellen Benutzers.
   * @return Aktualisierte Medium als Entity.
   * @throws RuntimeException Wenn der Benutzer das Medium nicht besitzt oder das Medium /die Kategorie nicht gefunden wurde.
   */
  public MediaEntity updateMedia(
    Long mediaId,
    MediaCreationDTO currentMedia,
    Authentication authentication
  ) {
    String username = authentication.getName();
    UserEntity user = userService
      .getUserByUsername(username)
      .orElseThrow(() -> new RuntimeException("User not found"));

    MediaEntity mediaToUpdate = mediaRepository
      .findById(mediaId)
      .orElseThrow(() -> new RuntimeException("Media not found"));

    // Sicherstellen, dass das Medium dem aktuellen Benutzer gehört (optional)
    if (!mediaToUpdate.getUser().getUserId().equals(user.getUserId())) {
      throw new RuntimeException(
        "You don't have permission to update this media."
      );
    }

    mediaToUpdate.setProducer(currentMedia.getProducer());
    mediaToUpdate.setTitle(currentMedia.getTitle());
    mediaToUpdate.setMediaState(
      MediaState.valueOf(currentMedia.getMediaState())
    );
    mediaToUpdate.setType(MediaType.valueOf(currentMedia.getType()));
    mediaToUpdate.setReleaseYear(currentMedia.getReleaseYear());
    mediaToUpdate.setNotes(currentMedia.getNotes());
    mediaToUpdate.setIsbn(currentMedia.getIsbn());
    mediaToUpdate.setIsFavorite(currentMedia.getIsFavorite());

    // Bestehende Kategorien entfernen und aus der Anfrage neu zuordnen
    mediaToUpdate.getMediaCategories().clear();

    if (currentMedia.getCategories() != null) {
      for (Long categoryId : currentMedia.getCategories()) {
        CategoryEntity categoryEntity = categoryRepository
          .findById(categoryId)
          .filter(cat -> cat.getUser().getUserId().equals(user.getUserId()))
          .orElseThrow(() ->
            new RuntimeException("Category not found or not owned by user.")
          );

        MediaCategory mc = new MediaCategory();
        mc.setMedia(mediaToUpdate);
        mc.setCategory(categoryEntity);
        mc.setCreatedAt(LocalDateTime.now());

        mediaToUpdate.getMediaCategories().add(mc);
      }
    }

    return mediaRepository.save(mediaToUpdate);
  }

    /**
   * Weist Medium eine neue Kategorie zu.
   *
   * @param mediaId ID des Mediums.
   * @param categoryId ID der hinzuzufügenden Kategorie.
   * @param authentication Authentifizierungsinformationen des aktuellen Benutzers.
   * @return Aktualisierte Medium mit hinzugefügten Kategorie.
   * @throws RuntimeException Wenn das Medium oder die Kategorie nicht gefunden wird, 
   * oder der Benutzer nicht berechtigt ist, Sachen zu ändern.
   */
  public MediaEntity assignCategoryToMedia(
    Long mediaId,
    Long categoryId,
    Authentication authentication
  ) {
    String userName = authentication.getName();

    MediaEntity media = mediaRepository
      .findById(mediaId)
      .orElseThrow(() ->
        new IllegalArgumentException("Media not found with ID: " + mediaId)
      );

     // Sicherstellen, dass der Benutzer das Medium besitzt
    if (!media.getUser().getUsername().equals(userName)) {
      throw new RuntimeException("You don't own this media.");
    }

    CategoryEntity category = categoryRepository
      .findById(categoryId)
      .filter(cat -> cat.getUser().getUsername().equals(userName))
      .orElseThrow(() ->
        new IllegalArgumentException("Category not found or not owned by user.")
      );

    // Überprüfen, ob die Kategorie bereits zugeordnet ist
    boolean exists = mediaCategoryRepository.existsByMediaMediaIdAndCategoryCategoryId(
      mediaId,
      categoryId
    );
    if (exists) {
      throw new IllegalArgumentException("Category is already assigned.");
    }

    MediaCategory mediaCategory = new MediaCategory();
    mediaCategory.setMedia(media);
    mediaCategory.setCategory(category);
    mediaCategory.setCreatedAt(LocalDateTime.now());

    mediaCategoryRepository.save(mediaCategory);

    // Rückgabe des aktualisierten Mediums
    return mediaRepository
      .findById(mediaId)
      .orElseThrow(() -> new RuntimeException("Error fetching updated media."));
  }

  /**
   * Entfernt Kategorie von einem Medium.
   *
   * @param mediaId ID des Mediums.
   * @param categoryId ID der zu entfernenden Kategorie.
   * @param authentication Authentifizierungsinformationen des aktuellen Benutzers.
   * @throws IllegalArgumentException Wenn das Medium oder die Kategorie nicht gefunden werden oder der Benutzer nicht berechtigt ist.
   */
  public void removeCategoryFromMedia(
    Long mediaId,
    Long categoryId,
    Authentication authentication
  ) {
    String username = authentication.getName();

    MediaEntity media = mediaRepository
      .findById(mediaId)
      .orElseThrow(() ->
        new IllegalArgumentException("Media not found with ID: " + mediaId)
      );

    if (!media.getUser().getUsername().equals(username)) {
      throw new RuntimeException("You don't own this media.");
    }

    CategoryEntity category = categoryRepository
      .findById(categoryId)
      .filter(cat -> cat.getUser().getUsername().equals(username))
      .orElseThrow(() ->
        new IllegalArgumentException(
          "Category not found or not owned by the user."
        )
      );

    MediaCategory mediaCategory = mediaCategoryRepository
      .findByMediaMediaIdAndCategoryCategoryId(mediaId, categoryId)
      .orElseThrow(() ->
        new IllegalArgumentException(
          "Category is not associated with this media."
        )
      );

    mediaCategoryRepository.delete(mediaCategory);
  }

  /**
   * Löscht Medium anhand seiner ID.
   * @param mediaId ID des zu löschenden Mediums.
   * @throws RuntimeException Wenn das Medium nicht gefunden wird.
   */
  public void deleteMedia(Long mediaId) {
    mediaRepository.deleteById(mediaId);
  }
}
