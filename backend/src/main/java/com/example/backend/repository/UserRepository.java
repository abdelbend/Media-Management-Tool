package com.example.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.UserEntity;

/**
 * Repository zur Verwaltung der {@link UserEntity}-Daten.
 * Stellt Methoden zum Abrufen und Überprüfen von Benutzerdaten in der Datenbank,
 * anhand des Benutzernamen und E-Mail zur Verfügung.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

   /**
   * Findet Benutzer anhand des Benutzernamens.
   * @param username Benutzername des gesuchten Benutzers.
   * @return Ein {@link Optional} mit dem gefundenen {@link UserEntity}, falls vorhanden.
   */
  Optional<UserEntity> findByUsername(String username);

  /**
   * Findet Benutzer anhand der E-Mail-Adresse.
   * @param email Gesuchte E-Mail-Adresse.
   * @return Ein {@link Optional} mit dem gefundenen {@link UserEntity}, falls vorhanden.
   */
  Optional<UserEntity> findByEmail(String email);


  /**
   * Überprüft, ob ein Benutzer mit dem angegebenen Benutzernamen bereits existiert.
   * 
   * @param username Zu überprüfender Benutzername.
   * @return {@code true}, wenn der Benutzername bereits existiert, sonst {@code false}.
   */
  Boolean existsByUsername(String username);

   /**
   * Überprüft, ob ein Benutzer mit der angegebenen E-Mail-Adresse bereits existiert.
   * @param email Zu überprüfende E-Mail-Adresse.
   * @return {@code true}, wenn die E-Mail-Adresse bereits existiert, sonst {@code false}.
   */
  Boolean existsByEmail(String email);
}
