package com.example.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.example.backend.model.PersonEntity;
import com.example.backend.model.UserEntity;
import com.example.backend.service.PersonService;
import com.example.backend.service.UserService;

/**
 * REST-Controller zur Verwaltung von Personen.
 * Stellt Möglichkeiten für CRUD-Operationen (Erstellen, Lesen, Aktualisieren, Löschen) zur Verfügung.
 */
@RestController
@RequestMapping("/api/persons")
public class PersonController {

  private final PersonService personService;

  @Autowired
  private UserService userService;

  /**
   * Konstruktor  {@code PersonController}.
   * 
   * @param personService Implementierung für die Logik für Person.
   */
  public PersonController(PersonService personService) {
    this.personService = personService;
  }

   /**
   * Gibt Person anhand ihrer ID zurück.
   * 
   * @param id ID der Person.
   * @return Die Person als {@link ResponseEntity}, falls gefunden, andernfalls 404 Not Found.
   */
  @GetMapping("/{id}")
  public ResponseEntity<PersonEntity> getPersonById(@PathVariable Long id) {
    return personService
      .getPersonById(id)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Gibt alle Personen zurück, die einem Benutzer basierend auf dem Benutzernamen zugeordnet sind.
   * 
   * @param authentication Authentifizierung des aktuellen Benutzers.
   * @return Eine Liste von {@link PersonEntity}, die dem Benutzer gehören.
   */
  @GetMapping("/by-username")
  public List<PersonEntity> getPersonsByUsername(
    Authentication authentication
  ) {
    String username = authentication.getName();
    return personService.getPersonsByUsername(username);
  }

   /**
   * Gibt alle Personen zurück, die einem Benutzer anhand der Benutzer-ID zugeordnet sind.
   * 
   * @param userId ID des Benutzers.
   * @return Eine Liste von {@link PersonEntity}, die dem Benutzer gehören.
   */
  @GetMapping("/user/{userId}")
  public List<PersonEntity> getPersonsByUserId(@PathVariable Long userId) {
    return personService.getPersonsByUserId(userId);
  }

  /**
   * Erstellt neue Person und verknüpft sie mit dem aktuellen Benutzer.
   * 
   * @param person Zu erstellende Person als {@link PersonEntity}.
   * @param authentication Authentifizierung des aktuellen Benutzers.
   * @return Erstellte Person.
   */
  @PostMapping
  public PersonEntity createPerson(
    @RequestBody PersonEntity person,
    Authentication authentication
  ) {
    String username = authentication.getName();
    Optional<UserEntity> userOptional = userService.getUserByUsername(username);
    UserEntity user = userOptional.get();
    person.setUser(user);
    return personService.createPerson(person, authentication);
  }

   /**
   * Aktualisiert bestehende Person.
   * 
   * @param personId ID der zu aktualisierenden Person.
   * @param currentPerson Aktualisierte Personendaten als {@link PersonEntity}.
   * @param authentication Authentifizierung des aktuellen Benutzers.
   * @return Die aktualisierte Person als {@link ResponseEntity}.
   */
  @PutMapping("/{personId}")
  public ResponseEntity<PersonEntity> updatePerson(
    @PathVariable Long personId,
    @RequestBody PersonEntity currentPerson,
    Authentication authentication
  ) {
    String username = authentication.getName();
    Optional<UserEntity> userOptional = userService.getUserByUsername(username);
    UserEntity user = userOptional.get();
    currentPerson.setUser(user);

    PersonEntity updatedPerson = personService.updatePerson(
      personId,
      currentPerson,
      authentication
    );

    return ResponseEntity.ok(updatedPerson);
  }
  
  /**
   * Löscht Person anhand ihrer ID, falls der Benutzer autorisiert ist.
   * 
   * @param id  ID der zu löschenden Person.
   * @param authentication Authentifizierung des aktuellen Benutzers.
   * @return Eine leere {@link ResponseEntity} mit dem entsprechenden HTTP-Status.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePerson(
    @PathVariable Long id,
    Authentication authentication
  ) {
    String username = authentication.getName();
    Optional<PersonEntity> personOptional = personService.getPersonById(id);

    if (personOptional.isEmpty()) {
      System.out.println("Person not found with ID: " + id);
      return ResponseEntity.notFound().build();
    }

    PersonEntity person = personOptional.get();

    // Prüft, ob der Benutzer berechtigt ist, die Person zu löschen
    if (!person.getUser().getUsername().equals(username)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    personService.deletePerson(id);
    return ResponseEntity.noContent().build();
  }
}
