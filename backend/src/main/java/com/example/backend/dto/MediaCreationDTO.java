package com.example.backend.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object, zur Erstellung eines Mediums.
 * Enthält erforderliche Informationen, um ein neues Medium zu erstellen.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaCreationDTO {

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
  private List<Long> categories = new ArrayList<>();
}
