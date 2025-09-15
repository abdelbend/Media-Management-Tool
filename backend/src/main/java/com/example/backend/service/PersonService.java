package com.example.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.backend.model.LoanEntity;
import com.example.backend.model.MediaEntity;
import com.example.backend.model.MediaState;
import com.example.backend.model.PersonEntity;
import com.example.backend.model.UserEntity;
import com.example.backend.repository.MediaRepository;
import com.example.backend.repository.PersonRepository;

/**
 * Service zur Verwaltung von Personen. Enthält Methoden zum Erstellen, 
 * Aktualisieren, Löschen und Abrufen von Personen, UND zum Verwalten von Ausleihen im Zusammenhang mit Personen.
 */
@Service
public class PersonService {

  private final PersonRepository personRepository;

   /**
   * Konstruktor
   * @param personRepository Repository für Verwaltung von Personen.
   * @param userService Service zur Verwaltung von Benutzern.
   */
  @Autowired
  private final UserService userService;

  @Autowired
  private MediaRepository mediaRepository;

  public PersonService(
    PersonRepository personRepository,
    UserService userService
  ) {
    this.userService = userService;
    this.personRepository = personRepository;
  }

   /**
   * Sucht nach Person anhand der ID.
   * @param personId ID der gesuchten Person.
   * @return Option, die die Person enthält, wenn sie gefunden wurde, sonst leer.
   */
  public Optional<PersonEntity> getPersonById(Long personId) {
    return personRepository.findById(personId);
  }

   /**
   * Holt alle Personen, die zu einem bestimmten Benutzer gehören.
   * @param userId ID des Benutzers.
   * @return Liste der Personen, die diesem Benutzer zugeordnet sind.
   */
  public List<PersonEntity> getPersonsByUserId(Long userId) {
    return personRepository.findByUserUserId(userId);
  }

    /**
   * Holt alle Personen, die einem Benutzer mit dem angegebenen Benutzernamen zugeordnet sind.
   * @param username Benutzername des Benutzers
   * @return Liste der Personen, die diesem Benutzer zugeordnet sind.
   * @throws RuntimeException Wenn der Benutzer nicht gefunden wird.
   */
  public List<PersonEntity> getPersonsByUsername(String username) {
    Optional<UserEntity> userOptional = userService.getUserByUsername(username);
    if (userOptional.isEmpty()) {
      throw new RuntimeException("User not found");
    }
    UserEntity user = userOptional.get();
    return personRepository.findByUserUserId(user.getUserId());
  }

    /**
   * Erstellt neue Person und weist sie dem aktuell authentifizierten Benutzer zu.
   * @param person Zu erstellende Person.
   * @param authentication Authentifizierungsinformationen des Benutzers.
   * @return Erstellte Person
   * @throws RuntimeException Wenn der Benutzer nicht gefunden wird.
   */
  public PersonEntity createPerson(
    PersonEntity person,
    Authentication authentication
  ) {
    String username = authentication.getName();
    Optional<UserEntity> userOptional = userService.getUserByUsername(username);
    if (userOptional.isEmpty()) {
      throw new RuntimeException("User not found");
    }
    UserEntity user = userOptional.get();
    person.setUser(user);
    return personRepository.save(person);
  }
  

  /**
   * Aktualisiert Daten einer bestehenden Person.
   * @param personId ID der zu aktualisierenden Person.
   * @param currentPerson Neuen Daten der Person.
   * @param authentication Authentifizierungsinformationen des Benutzers.
   * @return Aktualisierte Person.
   * @throws RuntimeException Wenn der Benutzer oder die Person nicht gefunden wird.
   */
  public PersonEntity updatePerson(
    Long personId,
    PersonEntity currentPerson,
    Authentication authentication
  ) {
    String username = authentication.getName();
    Optional<UserEntity> userOptional = userService.getUserByUsername(username);

    if (userOptional.isEmpty()) {
      throw new RuntimeException("User not found");
    }

    UserEntity user = userOptional.get();
    currentPerson.setUser(user);

    PersonEntity updatedPerson = personRepository
      .findById(personId)
      .orElseThrow(() -> new RuntimeException("Person not found"));

    updatedPerson.setFirstName(currentPerson.getFirstName());
    updatedPerson.setLastName(currentPerson.getLastName());
    updatedPerson.setAddress(currentPerson.getAddress());
    updatedPerson.setEmail(currentPerson.getEmail());
    updatedPerson.setPhone(currentPerson.getPhone());
    updatedPerson.setUser(currentPerson.getUser());
    return personRepository.save(updatedPerson);
  }

  /**
   * Löscht Person und gibt alle zugehörigen ausgeliehenen Medien als verfügbar zurück.
   * @param id ID der zu löschenden Person.
   * @throws RuntimeException Wenn beim Löschen ein Fehler auftritt.
   */
  public void deletePerson(Long id) {
    try {
      Optional<PersonEntity> person = personRepository.findById(id);

      if (person.isEmpty()) {
        throw new IllegalArgumentException(
          "Person with ID " + id + " does not exist."
        );
      }
      for (LoanEntity loan : person.get().getLoans()) {
        MediaEntity media = loan.getMedia();
        media.setMediaState(MediaState.AVAILABLE);
        mediaRepository.save(media);
        System.out.printf(
          "media id" + media.getMediaId() + "medianem" + media.getTitle() + "\n"
        );
      }
      personRepository.deleteById(id);
    } catch (Exception e) {
      throw new RuntimeException(
        "An error occurred while deleting person with ID " + id,
        e
      );
    }
  }
}
