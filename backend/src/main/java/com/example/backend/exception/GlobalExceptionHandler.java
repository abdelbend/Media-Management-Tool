package com.example.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Globale Ausnahmebehandlungs-Klasse, die spezifische Ausnahmen behandelt und 
 * benutzerdefinierte HTTP-Antworten zurückgibt.
 * Klasse wird mit {@link ControllerAdvice} markiert, um sie für alle Controller der Anwendung zu aktivieren.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Behandelt auftretende Ausnahme {@link MediaNotFoundException}, wenn ein Medium nicht gefunden wird.
   *
   * @param ex Ausgelöste {@code MediaNotFoundException}.
   * @return Eine {@link ResponseEntity}, die einen HTTP-Status 404 (Not Found) und die Fehlermeldung enthält.
   */
  @ExceptionHandler(MediaNotFoundException.class)
  public ResponseEntity<String> handleMediaNotFoundException(
    MediaNotFoundException ex
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  /**
   * Behandelt Ausnahme {@link PersonNotFoundException}, wenn eine Person nicht gefunden wird.
   *
   * @param ex Ausgelöste {@code PersonNotFoundException}.
   * @return Eine {@link ResponseEntity}, die einen HTTP-Status 404 (Not Found) und die Fehlermeldung enthält.
   */
  @ExceptionHandler(PersonNotFoundException.class)
  public ResponseEntity<String> handlePersonNotFoundException(
    PersonNotFoundException ex
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  /**
   * Behandelt Ausnahme {@link UserNotFoundException}, wenn ein Benutzer nicht gefunden wird.
   *
   * @param ex Ausgelöste {@code UserNotFoundException}.
   * @return Eine {@link ResponseEntity}, die einen HTTP-Status 404 (Not Found) und die Fehlermeldung enthält.
   */
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<String> handleUserNotFoundException(
    UserNotFoundException ex
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

   /**
   * Behandelt Ausnahme {@link LoginException}, wenn ein Problem bei der Anmeldung auftritt.
   *
   * @param ex Ausgelöste {@code LoginException}.
   * @return Eine {@link ResponseEntity}, die einen HTTP-Status 401 (Unauthorized) und die Fehlermeldung enthält.
   */
  @ExceptionHandler(LoginException.class)
  public ResponseEntity<String> handleLoginException(LoginException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }
}
