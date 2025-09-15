package com.example.backend.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.backend.model.LoanEntity;
import com.example.backend.model.MediaEntity;
import com.example.backend.model.MediaState;
import com.example.backend.model.PersonEntity;
import com.example.backend.model.UserEntity;
import com.example.backend.repository.LoanRepository;
import com.example.backend.repository.MediaRepository;
import com.example.backend.repository.PersonRepository;
import com.example.backend.repository.UserRepository;

import jakarta.transaction.Transactional;


/**
 * Integrationstests für den LoanController.
 * Tests prüfen die Funktionalitäten der Ausleihprozesse für Medien, 
 * inklusive der Erstellung von Ausleihen und der Anzeige von überfälligen Ausleihen.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class LoanControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MediaRepository mediaRepository;

  @Autowired
  private PersonRepository personRepository;

  @Autowired
  private LoanRepository loanRepository;

   /**
   * Testet die erfolgreiche Erstellung eines neuen Leihvorgangs.
   * Neues Benutzer-, Medien- und Personenobjekt wird angelegt
   * und der Leihvorgang mit einem festgelegten Fälligkeitsdatum durchgeführt.
   * 
   * @throws Exception Wenn ein Fehler bei der HTTP-Anfrage auftritt.
   */
  @Test
  void testCreateLoan() throws Exception {
    String uniqueUsername = "testuser2" + System.currentTimeMillis();
    UserEntity testUser = new UserEntity();
    testUser.setUsername(uniqueUsername);
    testUser.setPassword("password");
    testUser.setEmail(uniqueUsername + "@example.com");
    testUser = userRepository.save(testUser);

    MediaEntity testMedia = new MediaEntity();
    testMedia.setTitle("Test Media");
    testMedia.setMediaState(MediaState.AVAILABLE);
    testMedia.setType(com.example.backend.model.MediaType.BOOK);
    testMedia.setProducer("Test Producer");
    testMedia.setReleaseYear(2023);
    testMedia.setUser(testUser);
    testMedia = mediaRepository.save(testMedia);

    PersonEntity testPerson = new PersonEntity();
    testPerson.setFirstName("John");
    testPerson.setLastName("Doe");
    testPerson.setEmail("john.doe@example.com");
    testPerson.setAddress("123 Main St");
    testPerson.setPhone("01727560093");
    testPerson.setUser(testUser);
    testPerson = personRepository.save(testPerson);

    LocalDate dueDate = LocalDate.now().plusDays(14);
    LocalDateTime borrowedAt = LocalDateTime.now();

    LocalDateTime borrowedAtTruncated = borrowedAt.truncatedTo(
      ChronoUnit.SECONDS
    );

    SecurityContextHolder
      .getContext()
      .setAuthentication(
        new UsernamePasswordAuthenticationToken(
          testUser.getUsername(),
          null,
          List.of(new SimpleGrantedAuthority("ROLE_USER"))
        )
      );

    mockMvc
      .perform(
        post(
          "/api/loans/{mediaId}/{personId}",
          testMedia.getMediaId(),
          testPerson.getPersonId()
        )
          // dueDate as yyyy-MM-dd
          .param("dueDate", dueDate.toString())
          // borrowedAt as yyyy-MM-ddTHH:mm:ss
          .param(
            "borrowedAt",
            borrowedAtTruncated.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
          )
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isCreated());
  }


  /**
   * Testet den Fall, dass das angegebene Medium für die Ausleihe nicht gefunden wurde.
   * Zu erwarten: Eine 404-Fehlermeldung wird zurückgegeben.
   * 
   * @throws Exception Wenn ein Fehler bei der HTTP-Anfrage auftritt.
   */
  @Test
  @WithMockUser(username = "testuser", roles = { "USER" })
  void testCreateLoan_MediaNotFound() throws Exception {
    mockMvc
      .perform(
        post("/api/loans/{mediaId}/{personId}", 9999L, 1L)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNotFound());
  }

   /**
   * Testet den Fall, dass die angegebene Person für den Leihvorgang nicht gefunden wurde.
   * Zu erwarten: Eine 404-Fehlermeldung wird zurückgegeben.
   * 
   * @throws Exception Wenn ein Fehler bei der HTTP-Anfrage auftritt.
   */
  @Test
  @WithMockUser(username = "testuser", roles = { "USER" })
  void testCreateLoan_PersonNotFound() throws Exception {
    mockMvc
      .perform(
        post("/api/loans/{mediaId}/{personId}", 1L, 9999L)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNotFound());
  }

   /**
   * Testet das Abrufen von überfälligen Ausleihen.
   * Überfällige Ausleihe erstellt und deren Rückgabe überprüft.
   * Zu erwarten: Die überfällige Ausleihe wird erfolgreich zurückgegeben.
   * 
   * @throws Exception Wenn ein Fehler bei der HTTP-Anfrage auftritt.
   */
  @Test
  void testGetOverdueLoans() throws Exception {
    String uniqueUsername = "testuser" + System.currentTimeMillis();
    UserEntity testUser = new UserEntity();
    testUser.setUsername(uniqueUsername);
    testUser.setPassword("password");
    testUser.setEmail(uniqueUsername + "@example.com");
    userRepository.save(testUser);

    MediaEntity testMedia = new MediaEntity();
    testMedia.setTitle("Test Media");
    testMedia.setMediaState(MediaState.BORROWED);
    testMedia.setType(com.example.backend.model.MediaType.BOOK);
    testMedia.setProducer("Test Producer");
    testMedia.setReleaseYear(2023);
    testMedia.setUser(testUser);
    mediaRepository.save(testMedia);

    PersonEntity testPerson = new PersonEntity();
    testPerson.setFirstName("John");
    testPerson.setLastName("Doe");
    testPerson.setEmail("john.doe@example.com");
    testPerson.setAddress("123 Main St");
    testPerson.setPhone("01727560093");
    testPerson.setUser(testUser);
    personRepository.save(testPerson);

    LoanEntity overdueLoan = new LoanEntity();
    overdueLoan.setPerson(testPerson);
    overdueLoan.setMedia(testMedia);
    overdueLoan.setBorrowedAt(LocalDateTime.now().minusDays(10));
    overdueLoan.setDueDate(LocalDate.now().minusDays(1));
    overdueLoan.setReturnedAt(null);
    loanRepository.save(overdueLoan);

    SecurityContextHolder
      .getContext()
      .setAuthentication(
        new UsernamePasswordAuthenticationToken(
          testUser.getUsername(),
          null,
          List.of(new SimpleGrantedAuthority("ROLE_USER"))
        )
      );

    mockMvc
      .perform(
        get("/api/loans/overdue")
          .param("currentDate", LocalDate.now().toString())
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].person.firstName").value("John"))
      .andExpect(jsonPath("$[0].media.title").value("Test Media"));
  }

  /**
 * Testet das Abrufen von überfälligen Ausleihen für einen Benutzer, 
 * wobei ein Datum als Parameter übergeben wird.
 */
  @Test
  @WithMockUser(username = "testuser", roles = { "USER" })
  void testGetOverdueLoansByUser_withDateParameter() throws Exception {
    // Benutzer erstellen
    String uniqueUsername = "testuser" + System.currentTimeMillis();
    UserEntity testUser = new UserEntity();
    testUser.setUsername(uniqueUsername);
    testUser.setPassword("password");
    testUser.setEmail(uniqueUsername + "@example.com");
    userRepository.save(testUser);

    MediaEntity testMedia = new MediaEntity();
    testMedia.setTitle("Test Media");
    testMedia.setMediaState(MediaState.BORROWED);
    testMedia.setType(com.example.backend.model.MediaType.BOOK);
    testMedia.setProducer("Test Producer");
    testMedia.setReleaseYear(2023);
    testMedia.setUser(testUser);
    mediaRepository.save(testMedia);

    PersonEntity testPerson = new PersonEntity();
    testPerson.setFirstName("John");
    testPerson.setLastName("Doe");
    testPerson.setEmail("john.doe@example.com");
    testPerson.setAddress("123 Main St");
    testPerson.setPhone("01727560093");
    testPerson.setUser(testUser);
    personRepository.save(testPerson);

    LoanEntity overdueLoan = new LoanEntity();
    overdueLoan.setPerson(testPerson);
    overdueLoan.setMedia(testMedia);
    overdueLoan.setBorrowedAt(LocalDateTime.now().minusDays(10));
    overdueLoan.setDueDate(LocalDate.now().minusDays(1));
    overdueLoan.setReturnedAt(null);
    loanRepository.save(overdueLoan);

    SecurityContextHolder
      .getContext()
      .setAuthentication(
        new UsernamePasswordAuthenticationToken(
          testUser.getUsername(),
          null,
          List.of(new SimpleGrantedAuthority("ROLE_USER"))
        )
      );

    mockMvc
      .perform(
        get("/api/loans/overdue")
          .param("currentDate", LocalDate.now().toString())
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].person.firstName").value("John"))
      .andExpect(jsonPath("$[0].media.title").value("Test Media"))
      .andExpect(jsonPath("$.length()").value(1));
  }


/**
 * Testet das Markieren einer Ausleihe als "zurückgegeben".
 */
  @Test
  @WithMockUser(username = "testuser", roles = { "USER" })
  void testMarkAsReturned() throws Exception {
    String returnedAt = LocalDateTime
      .now()
      .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    String uniqueUsername = "testuser" + System.currentTimeMillis();
    UserEntity testUser = new UserEntity();
    testUser.setUsername(uniqueUsername);
    testUser.setPassword("password");
    testUser.setEmail(uniqueUsername + "@example.com");
    userRepository.save(testUser);

    MediaEntity testMedia = new MediaEntity();
    testMedia.setTitle("Test Media");
    testMedia.setMediaState(MediaState.BORROWED);
    testMedia.setType(com.example.backend.model.MediaType.BOOK);
    testMedia.setProducer("Test Producer");
    testMedia.setReleaseYear(2023);
    testMedia.setUser(testUser);
    mediaRepository.save(testMedia);

    PersonEntity testPerson = new PersonEntity();
    testPerson.setFirstName("John");
    testPerson.setLastName("Doe");
    testPerson.setEmail("john.doe@example.com");
    testPerson.setAddress("123 Main St");
    testPerson.setPhone("123456789");
    testPerson.setUser(testUser);
    personRepository.save(testPerson);

    LoanEntity testLoan = new LoanEntity();
    testLoan.setPerson(testPerson);
    testLoan.setMedia(testMedia);
    testLoan.setBorrowedAt(LocalDateTime.now().minusDays(5));
    testLoan.setDueDate(LocalDate.now().plusDays(10));
    testLoan.setReturnedAt(null);
    testLoan = loanRepository.save(testLoan);

    mockMvc
      .perform(
        put("/api/loans/{loanId}/return", testLoan.getLoanId())
          .contentType(MediaType.APPLICATION_JSON)
          .content("{\"returnedAt\": \"" + returnedAt + "\"}")
      )
      .andExpect(status().isNoContent());

    // Überprüfen, ob das Feld returnedAt gesetzt wurde
    LoanEntity updatedLoan = loanRepository
      .findById(testLoan.getLoanId())
      .orElseThrow();
    assertNotNull(updatedLoan.getReturnedAt());
  }

  /**
 * Testet das Abrufen aller aktiven Ausleihen eines Benutzers.
 */
  @Test
  void testGetActiveLoansByUser() throws Exception {
    // Benutzer erstellen
    String uniqueUsername = "testuser" + System.currentTimeMillis();
    UserEntity testUser = new UserEntity();
    testUser.setUsername(uniqueUsername);
    testUser.setPassword("password");
    testUser.setEmail(uniqueUsername + "@example.com");
    userRepository.save(testUser);
    System.out.println("Saved User ID: " + testUser.getUserId());

    MediaEntity testMedia = new MediaEntity();
    testMedia.setTitle("Test Media");
    testMedia.setMediaState(MediaState.AVAILABLE);
    testMedia.setType(com.example.backend.model.MediaType.BOOK);
    testMedia.setProducer("Test Producer");
    testMedia.setReleaseYear(2023);
    testMedia.setUser(testUser);
    mediaRepository.save(testMedia);
    System.out.println("Saved Media ID: " + testMedia.getMediaId());

    PersonEntity testPerson = new PersonEntity();
    testPerson.setFirstName("John");
    testPerson.setLastName("Doe");
    testPerson.setEmail("john.doe@example.com");
    testPerson.setAddress("123 Main St");
    testPerson.setPhone("01727560093");
    testPerson.setUser(testUser);
    personRepository.save(testPerson);
    System.out.println("Saved Person ID: " + testPerson.getPersonId());

    LoanEntity testLoan = new LoanEntity();
    testLoan.setPerson(testPerson);
    testLoan.setMedia(testMedia);
    testLoan.setBorrowedAt(LocalDateTime.now());
    testLoan.setDueDate(LocalDate.now().plusDays(14));
    testLoan.setReturnedAt(null);
    loanRepository.save(testLoan);
    System.out.println("Saved Loan ID: " + testLoan.getLoanId());

    SecurityContextHolder
      .getContext()
      .setAuthentication(
        new UsernamePasswordAuthenticationToken(
          testUser.getUsername(),
          null,
          List.of(new SimpleGrantedAuthority("ROLE_USER"))
        )
      );

    // Testanfrage senden und Erwartungen prüfen
    mockMvc
      .perform(get("/api/loans/active"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].person.firstName").value("John"))
      .andExpect(jsonPath("$[0].media.title").value("Test Media"));
  }


/**
 * Testet das Abrufen aller Ausleihen eines Benutzers.
 */
  @Test
  @WithMockUser(username = "testuser", roles = { "USER" })
  void testGetLoansByUser() throws Exception {
    String uniqueUsername = "testuser" + System.currentTimeMillis();
    UserEntity testUser = new UserEntity();
    testUser.setUsername(uniqueUsername);
    testUser.setPassword("password");
    testUser.setEmail(uniqueUsername + "@example.com");
    userRepository.save(testUser);

    MediaEntity testMedia = new MediaEntity();
    testMedia.setTitle("Test Media");
    testMedia.setMediaState(MediaState.AVAILABLE);
    testMedia.setType(com.example.backend.model.MediaType.BOOK);
    testMedia.setProducer("Test Producer");
    testMedia.setReleaseYear(2023);
    testMedia.setUser(testUser);
    mediaRepository.save(testMedia);

    PersonEntity testPerson = new PersonEntity();
    testPerson.setFirstName("John");
    testPerson.setLastName("Doe");
    testPerson.setEmail("john.doe@example.com");
    testPerson.setAddress("123 Main St");
    testPerson.setPhone("01727560093");
    testPerson.setUser(testUser);
    personRepository.save(testPerson);

    // Loan erstellen
    LoanEntity loan = new LoanEntity();
    loan.setBorrowedAt(LocalDateTime.now().minusDays(5));
    loan.setDueDate(LocalDate.now().plusDays(10));
    loan.setReturnedAt(null);
    loan.setPerson(testPerson);
    loan.setMedia(testMedia);
    loanRepository.save(loan);

    SecurityContextHolder
      .getContext()
      .setAuthentication(
        new UsernamePasswordAuthenticationToken(
          testUser.getUsername(),
          null,
          List.of(new SimpleGrantedAuthority("ROLE_USER"))
        )
      );

    // Testanfrage senden und Erwartungen prüfen
    mockMvc
      .perform(get("/api/loans/all"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].person.firstName").value("John"))
      .andExpect(jsonPath("$[0].media.title").value("Test Media"));
  }

  /**
 * Testet das Abrufen aller Ausleihen eines Benutzers, wenn der Benutzer nicht autorisiert ist.
 */
  @Test
  void testGetLoansByUser_Unauthorized() throws Exception {
    mockMvc.perform(get("/api/loans/all")).andExpect(status().isUnauthorized());
  }
}
