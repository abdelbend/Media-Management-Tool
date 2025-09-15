package com.example.backend.exception;

/**
 * Exception, die ausgelöst wird, wenn ein Benutzer nicht gefunden wird.
 * Sie zeigt an, dass der angeforderte User nicht existiert.
 */
public class UserNotFoundException extends RuntimeException {

  /**
   * Erstellt Instanz der {@code UserNotFoundException} mit der angegebenen Fehlermeldung.
   *
   * @param message Fehlermeldung, die die Ursache oder den Kontext des Fehlers beschreibt.
   */
  public UserNotFoundException(String message) {
    super(message);
  }
}
