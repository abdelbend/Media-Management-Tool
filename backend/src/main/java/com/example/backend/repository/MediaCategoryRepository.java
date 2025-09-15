package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.MediaCategory;

/**
 * Repository zur Verwaltung der {@link MediaCategory}-Daten.
 * Ermöglicht Zugriff auf {@link MediaCategory}-Datenbanktabelle und stellt
 * Methoden zum Abrufen und Überprüfen von Verknüpfungen zwischen Medien und Kategorien hzur Verfügung.
 */
@Repository
public interface MediaCategoryRepository
  extends JpaRepository<MediaCategory, Long> {
  /**
   * Findet Liste von {@link MediaCategory}-Objekten anhand der Kategorie-ID.
   * 
   * @param categoryId ID der Kategorie, nach der die Medienkategorien gesucht werden.
   * @return Eine Liste von {@link MediaCategory}-Objekten, die der angegebenen
   * Kategorie-ID zugeordnet sind.
   */
  List<MediaCategory> findByCategory_CategoryId(Long categoryId);

  /**
   * Überprüft, ob eine Verknüpfung zwischen einem bestimmten Medium und einer
   * bestimmten Kategorie existiert.
   * @param mediaId ID des Mediums, für das überprüft werden soll, ob eine Verknüpfung besteht.
   * @param categoryId ID der Kategorie, mit der das Medium verknüpft sein soll.
   * @return {@code true}, wenn eine Verknüpfung zwischen dem angegebenen Medium
   * und der angegebenen Kategorie existiert, sonst {@code false}.
   */
  boolean existsByMediaMediaIdAndCategoryCategoryId(
    Long mediaId,
    Long categoryId
  );

  /**
   * Findet {@link MediaCategory}-Verknüpfung zwischen einem bestimmten Medium
   * und einer bestimmten Kategorie.
   * @param mediaId ID des Mediums, für das die Verknüpfung abgerufen werden soll.
   * @param categoryId ID der Kategorie, mit der das Medium verknüpft sein soll.
   * @return Ein {@link Optional} mit der {@link MediaCategory}-Verknüpfung, falls
   * sie existiert, sonst ein leeres {@link Optional}.
   */
  Optional<MediaCategory> findByMediaMediaIdAndCategoryCategoryId(
    Long mediaId,
    Long categoryId
  );
}
