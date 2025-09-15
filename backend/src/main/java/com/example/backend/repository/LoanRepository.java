package com.example.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.model.LoanEntity;
import com.example.backend.model.PersonEntity;

/**
 * Repository zur Verwaltung der {@link LoanEntity}-Daten.
 * Ermöglicht Zugriff auf {@link LoanEntity}-Datenbanktabelle und stellt
 * Methoden zum Abrufen von Ausleihen anhand verschiedenen Kriterien zur Verfügung.
 */
@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long> {
  /**
   * Findet alle Ausleihen.
   * 
   * @return Liste aller {@link LoanEntity}-Objekte in der Datenbank.
   */
  @SuppressWarnings("null")
  @Override
  List<LoanEntity> findAll();

  /**
   * Findet Liste von Ausleihen des jeweiligen Benutzer, bei denen die
   * Rückgabe noch nicht erfolgt ist.
   * 
   * @param userId Die ID des Benutzers, dessen Ausleihen abgerufen werden sollen.
   * @return Liste von {@link LoanEntity}-Objekten für den jeweiligen Benutzer,
   *  bei denen das Rückgabedatum noch {@code null} ist.
   */
  List<LoanEntity> findByPerson_User_UserIdAndReturnedAtIsNull(Long userId);

  /**
   * Findet Liste von Ausleihen, die für eine Liste von Personen vorgenommen
   * wurden.
   * 
   * @param persons Liste von {@link PersonEntity}-Objekten, deren Ausleihen
   * abgerufen werden sollen.
   * @return Liste von {@link LoanEntity}-Objekten, die den jeweiligen
   * Personen zugeordnet sind.
   */
  @Query("SELECT l FROM LoanEntity l WHERE l.person IN :persons")
  List<LoanEntity> findByPersons(@Param("persons") List<PersonEntity> persons);

  /**
   * Findet Liste von Ausleihen für den jeweiligen Benutzer, deren Fälligkeit
   * vor einem bestimmten Datum liegt und bei denen die Rückgabe noch nicht erfolgt
   * ist.
   * 
   * @param userId ID des Benutzers, dessen überfällige Ausleihen abgerufen
   * werden sollen.
   * @param currentDate Aktuelle Datum, das als Grenze für die Fälligkeit
   * dient.
   * @return Liste von {@link LoanEntity}-Objekten für den jeweiligen
   * Benutzer, bei denen das Rückgabedatum noch {@code null} ist und das
   * Fälligkeitsdatum vor dem angegebenen  Datum {@code currentDate} liegt.
   */
  List<LoanEntity> findByPerson_User_UserIdAndDueDateBeforeAndReturnedAtIsNull(
    Long userId,
    LocalDate currentDate
  );

  /**
   * Findet alle Ausleihen, deren Rückgabe noch nicht erfolgt ist und deren
   * Fälligkeitsdatum dem angegebenen Datum entspricht.
   * 
   * @param dueDate Fälligkeitsdatum, nach dem die Ausleihen gefiltert werden sollen.
   * @return Liste von {@link LoanEntity}-Objekten, die die angegebenen Kriterien erfüllen.
   */
    @Query("SELECT l FROM LoanEntity l WHERE l.returnedAt IS NULL AND l.dueDate <= :dueDate")
    List<LoanEntity> findAllDueToday(@Param("dueDate") LocalDate dueDate);

}
