package com.example.backend.exception;

/**
 * Eine benutzerdefinierte Ausnahme, die ausgelöst wird, wenn ein Problem bei der Benutzeranmeldung auftritt.
 * Klasse wird so erweitert {@link RuntimeException}, sodass sie nicht explizit behandelt werden muss.
 */
public class LoginException extends RuntimeException {

  public LoginException(String message) {
    super(message);
  }
}
