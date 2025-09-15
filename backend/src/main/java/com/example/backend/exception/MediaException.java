package com.example.backend.exception;

/**
 * Eine benutzerdefinierte Ausnahmeklasse für Medienbezogene Fehler.
 * Diese Ausnahme wird verwendet, um spezifische Probleme bezüglich mit Medienoperationen zu anzuzeigen .
 */
public class MediaException extends RuntimeException {

  /**
   * Erstellt neue Instanz der {@code MediaException} mit angegebener Fehlermeldung.
   *
   * @param m Fehlermeldung, die die Ursache oder den Kontext des Fehlers beschreibt.
   */
  public MediaException(String m) {
    super(m);
  }
}
