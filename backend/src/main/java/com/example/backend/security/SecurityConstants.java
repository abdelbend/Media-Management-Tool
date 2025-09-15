package com.example.backend.security;

import java.security.Key;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;


/**
 * Enthält Sicherheitskonstanten, welche für JWT-Generierung und -Verifizierung verwendet werden.
 * Stellt den geheimen Schlüssel und die Token-Ablaufzeit für JSON Web Tokens zur Verfügung.
 */
public class SecurityConstants {

  /**
   * Ablaufzeit für ein JWT-Token in Millisekunden. 
   * Entspricht 2 Monaten (5184000000 ms).
   */
  public static final long JWT_EXPIRATION = 5184000000L;
  
  /**
   * Geheime Schlüssel für JWT-Signatur. 
   * Schlüssel wird für die HS256-Signatur verwendet, um JWT zu signieren und zu verifizieren.
   * Schlüssel wird sicher mit der {@link io.jsonwebtoken.security.Keys#secretKeyFor(SignatureAlgorithm)}-Methode erzeugt.
   */
  public static final Key JWT_SECRET_KEY = Keys.secretKeyFor(
    SignatureAlgorithm.HS256
  ); 
}
