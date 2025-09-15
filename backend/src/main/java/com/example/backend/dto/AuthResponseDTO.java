package com.example.backend.dto;

import lombok.Data;

/**
 * Ein DTO (Data Transfer Object) für die Darstellung der Authentifizierungsantwort.
 * Die Klasse enthält das Access Token und den Token-Typ für die Authentifizierung.
 */
@Data
public class AuthResponseDTO {

  /** Das Access-Token, das dem Benutzer zugewiesen wird. */
  private String accessToken;
  private String tokenType = "Bearer";  // Standardwert

  /**
   * Konstruktor zum Initialisieren des Access-Token und des Token-Typs.
   *
   * @param accessToken Access-Token für die Authentifizierung.
   * @param tokenType Typ des Tokens.
   */
  public AuthResponseDTO(String accessToken, String tokenType) {
    this.accessToken = accessToken;
    this.tokenType = tokenType;
  }

  /**
   * Konstruktor zur Initialisierung des Acess-Tokens. Token-Typ wird auf "Bearer" gesetzt.
   *
   * @param accessToken Access-Token für die Authentifizierung.
   */
  public AuthResponseDTO(String accessToken) {
    this.accessToken = accessToken;
  }
}
