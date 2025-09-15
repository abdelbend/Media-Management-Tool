package com.example.backend.security;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.backend.model.UserEntity;
import com.example.backend.repository.UserRepository;

/**
 * Implementierung von {@link UserDetailsService} für  Integration von 
 * Benutzer-Daten in das Spring Security Framework.
 * Stellt eine benutzerdefinierte Methode zum Laden von Benutzerinformationen
 * anhand des Benutzernamen zur Verfügung. Zuständig für die Authentifizierung
 * von Benutzern.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * Konstruktor fürs Initialisieren des {@link CustomUserDetailsService}.
   * @param userRepository Das Repository für die Verwaltung von Benutzerentitäten.
   */
  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

   /**
   * Lädt Benutzerinformationen anhand des Benutzernamen.
   * Wird von Spring Security während des Authentifizierungsprozesses
   * aufgerufen.
   * @param username Der Benutzername des Benutzers, der geladen werden soll.
   * @return {@link UserDetails} für den gefundenen Benutzer.
   * @throws UsernameNotFoundException Wenn kein Benutzer mit dem angegebenen
   * Benutzernamen gefunden wird.
   */
  @Override
  public UserDetails loadUserByUsername(String username)
    throws UsernameNotFoundException {
    UserEntity user = userRepository
      .findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    return new User(user.getUsername(), user.getPassword(), new ArrayList<>());
  }
}
