package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.model.PersonEntity;

/**
 * Repository zur Verwaltung der {@link PersonEntity}-Daten.
 * Ermöglicht Zugriff auf die {@link PersonEntity}-Datenbanktabelle und stellt
 * verschiedene Methoden zum Abrufen von Personendaten anhand verschiedenen Kriterien
 * zur Verfügung.
 */
@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Long> {

  /**
   * Findet alle Personen, die zu einem bestimmten Benutzer gehören, anhand der Benutzer-ID.
   * 
   * @param userId ID des Benutzers, dessen Personen abgerufen werden sollen.
   * @return Eine Liste von {@link PersonEntity} Objekten, die den angegebenen Benutzer repräsentieren.
   */
  List<PersonEntity> findByUserUserId(Long userId);

  /**
   * Findet Personen, deren Vorname mit einer Zeichnfolge beginnt und deren Nachname
   * mit einer bestimmten Zeichenfolge beginnt, wobei der Nachname optional ist.
   * 
   * @param firstName Vorname, der mit diesem Präfix beginnen soll.
   * @param lastName Nachname, der mit diesem Präfix beginnen soll. Kann {@code null} sein,
   * um nach Vorname zu filtern und den Nachnamen zu ignorieren.
   * @return Eine Liste von {@link PersonEntity} Objekten, die den angegebenen Kriterien entsprechen.
   */
  @Query(
    "SELECT p FROM PersonEntity p WHERE p.firstName LIKE :firstName% AND (:lastName IS NULL OR p.lastName LIKE :lastName%)"
  )
  List<PersonEntity> findByFirstNameStartsWithAndLastNameStartsWith(
    @Param("firstName") String firstName,
    @Param("lastName") String lastName
  );
}
