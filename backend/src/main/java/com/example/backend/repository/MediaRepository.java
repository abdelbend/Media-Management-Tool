package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend.dto.MediaWithCategoriesProjection;
import com.example.backend.model.MediaEntity;
import com.example.backend.model.MediaState;
import com.example.backend.model.MediaType;

/**
 * Repository zur Verwaltung der {@link MediaEntity}-Daten.
 * Ermöglicht Zugriff auf die {@link MediaEntity}-Datenbanktabelle und stellt
 * verschiedene Methoden zum Abrufen von Medienobjekten anahnd unterschiedlichen
 * Kriterien, inklusive ihrer Verknüpfungen mit Kategorien.
 */
@Repository
public interface MediaRepository extends JpaRepository<MediaEntity, Long> {

  /**
   * Findet Liste von Medien mit ihren zugehörigen Kategorien für einen bestimmten Benutzer.
   * Verwendet eine benutzerdefinierte SQL-Abfrage, um die Medien zusammen mit einer durch 
   * Kommas getrennten Liste von Kategorien zurückzugeben.
   * @param userId ID des Benutzers, dessen Medien und Kategorien abgerufen werden sollen.
   * @return Eine Liste von {@link MediaWithCategoriesProjection}, die Medien mit den zugehörigen Kategorien 
   * für den angegebenen Benutzer repräsentieren.
   */
  @Query(
    value = """
    SELECT 
        m.media_id                     AS mediaId,
        m.user_id                      AS userId,
        m.producer                     AS producer,
        m.title                        AS title,
        m.media_state                  AS mediaState,
        m.type                         AS type,
        m.release_year                 AS releaseYear,
        m.notes                        AS notes,
        m.isbn                         AS isbn,
        m.is_favorite                  AS isFavorite,
        m.created_at                   AS createdAt,
        group_concat(CONCAT(c.category_id, ':', c.category_name) SEPARATOR ',') AS categoryPairs
    FROM media m
    LEFT JOIN media_category mc ON m.media_id = mc.media_id
    LEFT JOIN category c        ON mc.category_id = c.category_id
    WHERE m.user_id = :userId
    GROUP BY 
        m.media_id, m.user_id, m.producer, m.title, 
        m.media_state, m.type, m.release_year, m.notes, 
        m.isbn, m.is_favorite, m.created_at
    """,
    nativeQuery = true
  )
  List<MediaWithCategoriesProjection> findMediaWithCategoriesByUserId(
    Long userId
  );

  /**
   * Findet alle Medien eines bestimmten Benutzers anhand der Benutzer-ID.
   * 
   * @param userId ID des Benutzers, dessen Medien abgerufen werden sollen.
   * @return Eine Liste von {@link MediaEntity} Objekten, die den angegebenen Benutzer repräsentieren.
   */
  List<MediaEntity> findByUserUserId(Long userId);

  /**
   * Findet alle Medien, die einem bestimmten {@link MediaState} zugeordnet sind.
   * @param mediaState Medienstatus, nach dem gesucht werden soll.
   * @return Eine Liste von {@link MediaEntity} Objekten, die den angegebenen Status haben.
   */
  List<MediaEntity> findByMediaState(MediaState mediaState);

   /**
   * Findet alle Medien, die einem bestimmten {@link MediaType} zugeordnet sind.
   * @param type Medientyp, nach dem gesucht werden soll.
   * @return Eine Liste von {@link MediaEntity} Objekten, die den angegebenen Typ haben.
   */
  List<MediaEntity> findByType(MediaType type);

  /**
   * Findet alle Medien, die als Favoriten markiert sind.
   * @param isFavorite Wert, der angibt, ob das Medium als Favorit markiert ist oder nicht.
   * @return Eine Liste von {@link MediaEntity} Objekten, die den angegebenen Favoritenstatus haben.
   */
  List<MediaEntity> findByIsFavorite(Boolean isFavorite);

  /**
   * Findet ein Medium anhand seiner ISBN-Nummer.
   * @param isbn ISBN des Mediums, nach dem gesucht werden soll.
   * @return Ein {@link Optional} mit dem gefundenen {@link MediaEntity}-Objekt, falls es existieren sollte.
   */
  Optional<MediaEntity> findByIsbn(String isbn);
}
