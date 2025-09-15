package com.example.backend.security;

import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Methoden, um ein Token zu generieren, den Benutzernamen aus 
 * einem Token zu extrahieren und das Token auf seine Gültigkeit zu
 * prüfen.
 */
@Component
public class JWTGenerator {

  /**
   * Generiert ein neues JWT-Token für den angegebenen Benutzer.
   * @param authentication Authentication-Objekt, das Informationen über den
   *  authentifizierten Benutzer enthält.
   * @return JWT-Token als String.
   */
  public String generateToken(Authentication authentication) {
    String username = authentication.getName();
    Date currentDate = new Date();
    Date expiryDate = new Date(
      currentDate.getTime() + SecurityConstants.JWT_EXPIRATION
    );

    // JWT erstellen
    String token = Jwts
      .builder()
      .setSubject(username)
      .setIssuedAt(currentDate)
      .setExpiration(expiryDate)
      .signWith(SecurityConstants.JWT_SECRET_KEY, SignatureAlgorithm.HS256)
      .compact();

    return token;
  }

  /**
   * Extrahiert Benutzernamen aus angegebenen JWT-Token.
   * @param token JWT-Token, aus dem der Benutzername extrahiert werden soll.
   * @return Benutzername des JWT-Tokens.
   */
  public String getUserNameFromJWT(String token) {
    Claims claims = Jwts
      .parserBuilder()
      .setSigningKey(SecurityConstants.JWT_SECRET_KEY)
      .build()
      .parseClaimsJws(token)
      .getBody();
    return claims.getSubject();
  }

   /**
   * Validiert angegebenen JWT-Token und prüft, ob es korrekt signiert ist und
   * nicht abgelaufen ist.
   * @param token JWT-Token, das validiert werden soll.
   * @return {@code true} wenn das Token gültig ist, sonst wird eine
   * {@link IllegalArgumentException} geworfen.
   * @throws IllegalArgumentException Wenn das Token abgelaufen oder ungültig ist.
   */
  public boolean validateToken(String token) {
    try {
      Jwts
        .parserBuilder()
        .setSigningKey(SecurityConstants.JWT_SECRET_KEY)
        .build()
        .parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      throw new IllegalArgumentException("JWT token is expired or invalid");
    }
  }
}
