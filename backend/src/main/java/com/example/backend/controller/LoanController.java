package com.example.backend.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.exception.MediaNotFoundException;
import com.example.backend.exception.PersonNotFoundException;
import com.example.backend.model.LoanEntity;
import com.example.backend.service.LoanService;

/**
 * REST-Controller zur Verwaltung von Ausleihen.
 * Stellt Möglichkeiten für CRUD-Operationen (Erstellen, Lesen, Aktualisieren, Löschen) zur Verfügung.
 */
@RestController
@RequestMapping("/api/loans")
public class LoanController {

  @Autowired
  private final LoanService loanService;

  /**
   * Konstruktor {@code LoanController}.
   * 
   * @param loanService Implementiert Logik zu Ausleihen.
   */
  public LoanController(LoanService loanService) {
    this.loanService = loanService;
  }

  /**
   * Gibt alle Ausleihen des aktuell authentifizierten Benutzers zurück.
   * 
   * @return Eine {@link ResponseEntity} mit einer Liste von {@link LoanEntity}.
   */
  @GetMapping("/all")
  public ResponseEntity<List<LoanEntity>> getLoansByUser() {
    List<LoanEntity> loans = loanService.getLoansByUser();
    return new ResponseEntity<>(loans, HttpStatus.OK);
  }

  /**
   * Gibt alle aktiven Ausleihen des aktuell authentifizierten Benutzers zurück.
   * 
   * @return Eine {@link ResponseEntity} mit einer Liste von {@link LoanEntity}, die noch nicht zurückgegeben wurden.
   */
  @GetMapping("/active")
  public ResponseEntity<List<LoanEntity>> getActiveLoansByUser() {
    List<LoanEntity> activeLoans = loanService.getActiveLoansByUser();
    return new ResponseEntity<>(activeLoans, HttpStatus.OK);
  }

   /**
   * Gibt alle überfälligen Ausleihen des aktuell authentifizierten Benutzers zurück.
   * 
   * @param currentDate Genutzt um Überfälligkeit zu überprüfen. Standardmäßig wird das heutige Datum verwendet.
   *                    Ist optional.
   * @return Eine {@link ResponseEntity} mit einer Liste von {@link LoanEntity}, die überfällig sind.
   */
  @GetMapping("/overdue")
  public ResponseEntity<List<LoanEntity>> getOverdueLoansByUser(
    @RequestParam(required = false) LocalDate currentDate
  ) {
    if (currentDate == null) {
      currentDate = LocalDate.now(); // Standardwert: heutiges Datum
    }
    List<LoanEntity> overdueLoans = loanService.getOverdueLoansByUser(
      currentDate
    );
    return new ResponseEntity<>(overdueLoans, HttpStatus.OK);
  }

  /**
   * Ausleihe als "zurückgegeben" markieren.
   * 
   * @param loanId ID der Ausleihe, die als zurückgegeben markiert werden soll.
   * @param payload Map, mit Zeitpunkt der Rückgabe (z. B. "returnedAt").
   * @return Eine leere {@link ResponseEntity} mit dem HTTP-Status "204 No Content".
   */
  @PutMapping("/{loanId}/return")
  public ResponseEntity<Void> markAsReturned(
    @PathVariable Long loanId,
    @RequestBody Map<String, String> payload
  ) {
    String returnedAtString = payload.get("returnedAt");
    LocalDateTime returnedAt = LocalDateTime.parse(
      returnedAtString,
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    );
    loanService.markAsReturned(loanId, returnedAt);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Erstellt neue Ausleihe für ein bestimmtes Medium und eine bestimmte Person.
   * 
   * @param mediaId ID des Mediums, das ausgeliehen werden soll.
   * @param personId ID der Person, die das Medium ausleiht.
   * @param authentication Authentifizierung des aktuellen Benutzers.
   * @param dueDate Fälligkeitsdatum der Ausleihe. Ist optional.
   * @param borrowedAt Zeitpunkt der Ausleihe. Ist optional.
   * @return Eine {@link ResponseEntity}, die die erstellte {@link LoanEntity} enthält.
   * @throws ResponseStatusException Exception für den Fall, dass das Medium oder die Person nicht gefunden wird.
   */
  @PostMapping("/{mediaId}/{personId}")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<LoanEntity> createLoan(
    @PathVariable Long mediaId,
    @PathVariable Long personId,
    Authentication authentication,
    @RequestParam(required = false) LocalDate dueDate,
    @RequestParam(required = false) LocalDateTime borrowedAt
  ) {
    try {
      LoanEntity createdLoan = loanService.createLoan(
        mediaId,
        personId,
        authentication,
        dueDate,
        borrowedAt
      );
      return ResponseEntity.status(HttpStatus.CREATED).body(createdLoan);
    } catch (MediaNotFoundException | PersonNotFoundException e) {
      throw new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        e.getMessage(),
        e
      );
    }
  }
}
