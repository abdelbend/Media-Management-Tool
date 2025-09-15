package com.example.backend.exception;

/**
 * Eine benutzerdefinierte Ausnahme, die ausgelöst wird, wenn ein Medium nicht gefunden wird.
 * Diese Ausnahme zeigt an, dass die angeforderte Medienressource nicht existiert.
 */

public class MediaNotFoundException extends RuntimeException {

  /**
   * Erstellt neue Instanz der {@code MediaNotFoundException} mit der angegebenen Fehlermeldung.
   *
   * @param message Fehlermeldung, die die Ursache oder den Kontext des Fehlers beschreibt.
   */
  public MediaNotFoundException(String message) {
    super(message);
  }
}
