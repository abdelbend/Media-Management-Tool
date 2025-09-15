package com.example.backend.exception;

/**
 * Benutzerdefinierte Ausnahme, die ausgelöst wird, wenn eine Person nicht gefunden wird.
 * Diese Ausnahme zeigt an, dass die angeforderte Personendatenressource nicht existiert.
 */
public class PersonNotFoundException extends RuntimeException {

  /**
   * Erstellt neue Instanz der {@code PersonNotFoundException} mit der angegebenen Fehlermeldung.
   *
   * @param message Fehlermeldung, die die Ursache oder den Kontext des Fehlers beschreibt.
   */
  public PersonNotFoundException(String message) {
    super(message);
  }
}
