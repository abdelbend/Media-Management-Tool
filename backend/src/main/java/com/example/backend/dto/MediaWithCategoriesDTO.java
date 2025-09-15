package com.example.backend.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Data Transfer Object, für die Darstellung eines Medium mit den zugehörigen Kategorien.
 * Verwendet, um Informationen über ein Medium und dessen Kategorien an die API-Benutzer zu übertragen.
 */
@Data
public class MediaWithCategoriesDTO {

  private Long mediaId;
  private Long userId;

  private String producer;
  private String title;
  private String mediaState;
  private String type;
  private Integer releaseYear;
  private String notes;
  private String isbn;
  private Boolean isFavorite;
  private LocalDateTime createdAt;

  /** Eine Liste von Kategorien, die dem Medium zugeordnet sind. */
  private List<CategoryDTO> categories = new ArrayList<>();

  public MediaWithCategoriesDTO() {}

  /**
   * Konstruktor, der ein {@link MediaCreationDTO} als Eingabe verwendet.
   * Übernimmt die Felder aus dem MediaCreationDTO und erstellt eine neue Instanz von MediaWithCategoriesDTO.
   *
   * @param media MediaCreationDTO mit Basisinformationen.
   */
  public MediaWithCategoriesDTO(MediaCreationDTO media) {
    this.producer = media.getProducer();
    this.title = media.getTitle();
    this.mediaState = media.getMediaState();
    this.type = media.getType();
    this.releaseYear = media.getReleaseYear();
    this.notes = media.getNotes();
    this.isbn = media.getIsbn();
    this.isFavorite = media.getIsFavorite();
    this.createdAt = media.getCreatedAt();
  }

  /**
   * Methode erstellt MediaWithCategoriesDTO-Objekt aus einer {@link MediaWithCategoriesProjection}.
   * Sie verarbeitet die Kategorien und erstellt entsprechende {@link CategoryDTO}-Instanzen.
   *
   * @param projection Projektion mit den Medien- und Kategoriedaten.
   * @return Vollständig initialisiertes MediaWithCategoriesDTO-Objekt.
   */
  public static MediaWithCategoriesDTO fromProjection(
    MediaWithCategoriesProjection projection
  ) {
    MediaWithCategoriesDTO dto = new MediaWithCategoriesDTO();
    dto.setMediaId(projection.getMediaId());
    dto.setUserId(projection.getUserId());
    dto.setProducer(projection.getProducer());
    dto.setTitle(projection.getTitle());
    dto.setMediaState(projection.getMediaState());
    dto.setType(projection.getType());
    dto.setReleaseYear(projection.getReleaseYear());
    dto.setNotes(projection.getNotes());
    dto.setIsbn(projection.getIsbn());
    dto.setIsFavorite(projection.getIsFavorite());
    dto.setCreatedAt(projection.getCreatedAt());

    if (projection.getCategoryPairs() != null) {
      String[] pairs = projection.getCategoryPairs().split(",");
      for (String pair : pairs) {
        String[] parts = pair.split(":");
        if (parts.length == 2) {
          Long catId = Long.valueOf(parts[0]);
          String catName = parts[1];
          CategoryDTO categoryDTO = new CategoryDTO();
          categoryDTO.setCategoryId(catId);
          categoryDTO.setCategoryName(catName);
          dto.getCategories().add(categoryDTO);
        }
      }
    }
    return dto;
  }
}
