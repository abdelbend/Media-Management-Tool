package com.example.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.UserEntity;
import com.example.backend.service.UserService;

/**
 * REST-Controller für die Verwaltung von Benutzern.
 * Stellt Möglichkeiten für CRUD-Operationen (Erstellen, Lesen, Aktualisieren, Löschen) zur Verfügung.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  /**
   * Konstruktor  {@code UserController}.
   *
   * @param userService Implementierung der Logik für Benutzer.
   */
  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Wenn es höchstens fünf Benutzer gibt, wird eine Liste von Benutzern
   * zurückgegeben.
   * Falls keine Benutzer vorhanden sind, wird ein 204 No Content zurückgegeben.
   *
   * @return Eine Liste von Benutzern als {@link ResponseEntity}.
   */
  @GetMapping("/returnUsers")
  public ResponseEntity<List<UserEntity>> getUsersIfFiveOrLess() {
    List<UserEntity> users = userService.getUsersIfFiveOrLess();
    if (users.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(users);
  }

  /**
   * Ruft Benutzer anhand seines Benutzernamens ab.
   *
   * @param username Benutzername des gesuchten Benutzers.
   * @return Der Benutzer als {@link ResponseEntity}, falls gefunden, sonst 404 Not Found.
   */
  @GetMapping("/{username}")
  public ResponseEntity<UserEntity> getUserByUsername(
    @PathVariable String username
  ) {
    return userService
      .getUserByUsername(username)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Aktualisiert Daten eines Benutzers.
   *
   * @param id ID des zu aktualisierenden Benutzers.
   * @param user Neue Daten für den Benutzer.
   * @return Der aktualisierte Benutzer als {@link ResponseEntity}.
   */
  @PutMapping("/{id}")
  public ResponseEntity<UserEntity> updateUser(
    @PathVariable Long id,
    @RequestBody UserEntity user
  ) {
    return ResponseEntity.ok(userService.updateUser(id, user));
  }

  /**
   * Löscht Benutzer anhand seiner ID.
   *
   * @param id Die ID des zu löschenden Benutzers.
   * @return Eine leere {@link ResponseEntity} mit dem Status 204 No Content.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}
