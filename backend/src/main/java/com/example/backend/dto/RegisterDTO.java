package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object zur Repräsentation von Benutzerdaten für die Registrierung.
 * Enthält Informationen, welche für die Erstellung eines neuen Benutzers benötigt werden.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {

  private String username;

  private String password;

  private String email;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
