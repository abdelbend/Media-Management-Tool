package com.example.backend.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.backend.model.UserEntity;
import com.example.backend.repository.UserRepository;

/**
 * Service zur Verwaltung von Benutzern. Enthält Methoden zum Abrufen, Aktualisieren 
 * und Löschen von Benutzerdaten.
 */
@Service
public class UserService {

  private final UserRepository userRepository;

   /**
   * Konstruktor
   * @param userRepository Repository zur Verwaltung der Benutzer.
   */
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Holt Benutzer, wenn Anzahl der Benutzer 5 oder weniger beträgt.
   * @return Liste aller Benutzer, wenn Anzahl der Benutzer 5 oder weniger beträgt, sonst eine leere Liste.
   */
  public List<UserEntity> getUsersIfFiveOrLess() {
    long userCount = userRepository.count();
    if (userCount <= 5) {
      return userRepository.findAll();
    } else {
      return Collections.emptyList();
    }
  }


  /**
   * Sucht nach Benutzer anhand des Benutzernamens.
   * @param username Benutzername des gesuchten Benutzers.
   * @return Option, die den Benutzer enthält, wenn er gefunden wurde, sonst leer.
   */
  public Optional<UserEntity> getUserByUsername(String username) {
    return userRepository.findByUsername(username);
  }

   /**
   * Aktualisiert Daten eines bestehenden Benutzers.
   * @param userId ID des zu aktualisierenden Benutzers.
   * @param userDetails Neuen Details des Benutzers.
   * @return Aktualisierter Benutzer.
   * @throws RuntimeException Wenn der Benutzer nicht gefunden wird.
   */
  public UserEntity updateUser(Long userId, UserEntity userDetails) {
    UserEntity user = userRepository
      .findById(userId)
      .orElseThrow(() -> new RuntimeException("User not found"));
    user.setUsername(userDetails.getUsername());
    user.setPassword(userDetails.getPassword());
    user.setEmail(userDetails.getEmail());
    return userRepository.save(user);
  }
  
  /**
   * Löscht Benutzer anhand der ID.
   * @param userId ID des zu löschenden Benutzers.
   */
  public void deleteUser(Long userId) {
    userRepository.deleteById(userId);
  }
}
