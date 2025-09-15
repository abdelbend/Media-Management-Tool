package com.example.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.backend.exception.MediaNotFoundException;
import com.example.backend.exception.PersonNotFoundException;
import com.example.backend.exception.UserNotFoundException;
import com.example.backend.model.LoanEntity;
import com.example.backend.model.MediaEntity;
import com.example.backend.model.MediaState;
import com.example.backend.model.PersonEntity;
import com.example.backend.model.UserEntity;
import com.example.backend.repository.LoanRepository;
import com.example.backend.repository.MediaRepository;
import com.example.backend.repository.PersonRepository;
import com.example.backend.repository.UserRepository;


/**
 * Für erwaltung von Ausleihen. Enthält Methoden zum Erstellen von Ausleihen, Abfragen
 * von Ausleihen und Markieren von Medien als zurückgegeben.
 */
@Service
public class LoanService {

  @Autowired
  private final UserService userService;

  private final MediaService mediaService;
  private final MediaRepository mediaRepository;
  private final PersonService personService;
  private final LoanRepository loanRepository;
  private final UserRepository userRepository;
  private final PersonRepository personRepository;

   /**
   * Konstruktor  {@link LoanService}.
   * @param loanRepository Repository für Ausleihen.
   * @param userService Service für Benutzer.
   * @param mediaService Service für Medien.
   * @param mediaRepository Repository für Medien.
   * @param personService Service für Personen.
   * @param personRepository Repository für Personen.
   * @param userRepository Repository für Benutzer.
   */
  public LoanService(
    LoanRepository loanRepository,
    UserService userService,
    MediaService mediaService,
    MediaRepository mediaRepository,
    PersonService personService,
    PersonRepository personRepository,
    UserRepository userRepository
  ) {
    this.mediaService = mediaService;
    this.mediaRepository = mediaRepository;
    this.personService = personService;
    this.userService = userService;
    this.loanRepository = loanRepository;
    this.personRepository = personRepository;
    this.userRepository = userRepository;
  }

   /**
   * Gibt alle Ausleihen für den aktuellen Benutzer zurück.
   * @return Liste der Ausleihen für den aktuellen Benutzer.
   */
  public List<LoanEntity> getLoansByUser() {
    String username = SecurityContextHolder
      .getContext()
      .getAuthentication()
      .getName();
    Optional<UserEntity> user = userRepository.findByUsername(username);

    List<PersonEntity> persons = personRepository.findByUserUserId(
      user.get().getUserId()
    );
    if (persons.isEmpty()) {
      return Collections.emptyList();
    }
    return loanRepository.findByPersons(persons);
  }


  /**
   * Gibt alle aktiven Ausleihen, die noch nicht zurückgegeben worden sind,
   * für den aktuellen Benutzer zurück.
   * @return Liste der aktiven Ausleihen für den aktuellen Benutzer.
   */
  public List<LoanEntity> getActiveLoansByUser() {
    String username = SecurityContextHolder
      .getContext()
      .getAuthentication()
      .getName();
    Optional<UserEntity> user = userRepository.findByUsername(username);

    if (user.isEmpty()) {
      throw new UsernameNotFoundException(
        "User not found with username: " + username
      );
    }

    return loanRepository.findByPerson_User_UserIdAndReturnedAtIsNull(
      user.get().getUserId()
    );
  }
  
  /**
   * Gibt alle überfälligen Ausleihen für den aktuellen Benutzer zurück.
   * @param currentDate Aktuelles Datum.
   * @return Liste der überfälligen Ausleihen für den aktuellen Benutzer.
   */
  public List<LoanEntity> getOverdueLoansByUser(LocalDate currentDate) {
    String username = SecurityContextHolder
      .getContext()
      .getAuthentication()
      .getName();
    Optional<UserEntity> user = userRepository.findByUsername(username);

    if (user.isEmpty()) {
      throw new UsernameNotFoundException(
        "User not found with username: " + username
      );
    }

    return loanRepository.findByPerson_User_UserIdAndDueDateBeforeAndReturnedAtIsNull(
      user.get().getUserId(),
      currentDate
    );
  }

    /**
   * Erstellt neue Ausleihe für ein Medium und eine Person.
   *
   * @param mediaId ID des Mediums.
   * @param personId ID der Person, die das Medium ausleiht.
   * @param authentication Authentifizierungsobjekt des aktuellen Benutzers.
   * @param dueDate Fälligkeitsdatum der Ausleihe.
   * @param borrowedAt  Datum und die Uhrzeit, an dem das Medium ausgeliehen wurde.
   * @return Erstellte {@link LoanEntity}-Objekt.
   */
  public LoanEntity createLoan(
    Long mediaId,
    Long personId,
    Authentication authentication,
    LocalDate dueDate,
    LocalDateTime borrowedAt
  ) {
    String username = authentication.getName();

    Optional<UserEntity> userOptional = userService.getUserByUsername(username);
    if (userOptional.isEmpty()) {
      throw new UserNotFoundException("User not found");
    }
    @SuppressWarnings("unused")
    UserEntity user = userOptional.get();

    Optional<MediaEntity> mediaOptional = mediaService.getMediaById(mediaId);
    if (mediaOptional.isEmpty()) {
      throw new MediaNotFoundException("Media not found with ID: " + mediaId);
    }
    MediaEntity media = mediaOptional.get();
    if (
      media.getMediaState() == MediaState.BORROWED ||
      media.getMediaState() == MediaState.UNAVAILABLE
    ) {
      throw new RuntimeException("Media is not available for loan");
    }

    Optional<PersonEntity> personOptional = personService.getPersonById(
      personId
    );
    if (personOptional.isEmpty()) {
      throw new PersonNotFoundException(
        "Person not found with ID: " + personId
      );
    }
    PersonEntity person = personOptional.get();
    LoanEntity loan = new LoanEntity();
    loan.setPerson(person);
    loan.setMedia(media);
    loan.setBorrowedAt(borrowedAt == null ? LocalDateTime.now() : borrowedAt);
    loan.setDueDate(dueDate == null ? LocalDate.now().plusMonths(1) : dueDate);

    media.setMediaState(MediaState.BORROWED);
    mediaRepository.save(media);
    LoanEntity savedLoan = loanRepository.save(loan);
    MediaEntity savedMedia = media;
    savedMedia.setMediaCategories(Collections.emptySet());
    savedLoan.setMedia(savedMedia);
    return savedLoan;
  }

    /**
   * Markiert Ausleihe als "zurückgegeben" und aktualisiert Medienstatus.
   * @param loanId ID der Ausleihe, die als zurückgegeben markiert werden soll.
   * @param returnedAt Datum und Uhrzeit der Rückgabe des Mediums.
   */
  public void markAsReturned(Long loanId, LocalDateTime returnedAt) {
    if (loanId == null) {
      throw new IllegalArgumentException("Loan ID cannot be null");
    }
    LoanEntity loan = loanRepository
      .findById(loanId)
      .orElseThrow(() ->
        new RuntimeException("Loan not found with ID: " + loanId)
      );
    if (loan.getReturnedAt() != null) {
      throw new RuntimeException("Loan already returned with ID: " + loanId);
    }
    if (returnedAt == null) {
      returnedAt = LocalDateTime.now();
    }
    LocalDateTime borrowedAt = loan.getBorrowedAt();
    if (borrowedAt == null) {
      throw new RuntimeException(
        "Borrowed date is not set for loan with ID: " + loanId
      );
    }

    if (returnedAt.isBefore(borrowedAt)) {
      throw new IllegalArgumentException(
        "Returned date cannot be before borrowed date for loan with ID: " +
        loanId
      );
    }

    loan.setReturnedAt(returnedAt);
    loanRepository.save(loan);

    MediaEntity media = loan.getMedia();
    media.setMediaState(MediaState.AVAILABLE);
    mediaRepository.save(media);
  }
}
