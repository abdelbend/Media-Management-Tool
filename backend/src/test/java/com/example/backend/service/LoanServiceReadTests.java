package com.example.backend.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.backend.model.LoanEntity;
import com.example.backend.model.PersonEntity;
import com.example.backend.model.UserEntity;
import com.example.backend.repository.LoanRepository;
import com.example.backend.repository.PersonRepository;
import com.example.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class LoanServiceReadTests {

  @Mock
  private LoanRepository loanRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PersonRepository personRepository;

  @InjectMocks
  private LoanService loanService;

  @BeforeEach
  void setupSecurityContext() {
    // Mock SecurityContext
    SecurityContext context = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    lenient().when(authentication.getName()).thenReturn("testuser");
    lenient().when(context.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(context);
  }

  /**
 * Testet das Abrufen der Leihvorgänge für den eingeloggten Benutzer.
 * Zu erwarten:Liste von Leihvorgängen zurückgegeben, wenn der Benutzer existiert
 * und mindestens eine zugeordnete Person mit Leihvorgängen vorhanden ist.
 */
  @Test
  void testGetLoansByUser_shouldReturnListOfLoansForLoggedInUser() {
    String username = "testuser";
    UserEntity userEntity = new UserEntity();
    userEntity.setUserId(1L);
    userEntity.setUsername(username);

    PersonEntity personEntity = new PersonEntity();
    personEntity.setPersonId(10L);
    personEntity.setUser(userEntity);

    LoanEntity loan1 = new LoanEntity();
    LoanEntity loan2 = new LoanEntity();
    List<LoanEntity> loans = List.of(loan1, loan2);

    when(userRepository.findByUsername(username))
      .thenReturn(Optional.of(userEntity));
    when(personRepository.findByUserUserId(1L))
      .thenReturn(Collections.singletonList(personEntity));
    when(loanRepository.findByPersons(Collections.singletonList(personEntity)))
      .thenReturn(loans);

    // Act
    List<LoanEntity> result = loanService.getLoansByUser();

    assertEquals(2, result.size());
    assertTrue(result.contains(loan1));
    assertTrue(result.contains(loan2));
  }


/**
 * Testet das Abrufen der Leihvorgänge für den eingeloggten Benutzer, wenn der Benutzer nicht gefunden wird.
 * Zu erwarten: `NoSuchElementException` geworfen, wenn der Benutzer nicht existiert.
 */
  @Test
  void testGetLoansByUser_whenUserNotFound_shouldThrowException() {
    String username = "testuser";

    SecurityContext context = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    when(authentication.getName()).thenReturn(username);
    when(context.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(context);

    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    NoSuchElementException exception = assertThrows(
      NoSuchElementException.class,
      () -> loanService.getLoansByUser()
    );
    assertEquals("No value present", exception.getMessage()); // Erwartete Nachricht von NoSuchElementException
  }

  /**
 * Testet das Abrufen der Leihvorgänge für den eingeloggten Benutzer, wenn keine Personen für den Benutzer gefunden werden.
 * Zu erwarten: Leere Liste zurückgegeben, wenn keine Personen mit Leihvorgängen für den Benutzer existieren.
 */
  @Test
  void testGetLoansByUser_whenNoPersonsFound_shouldReturnEmptyList() {
    String username = "testuser";
    UserEntity userEntity = new UserEntity();
    userEntity.setUserId(1L);

    SecurityContext context = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    when(authentication.getName()).thenReturn(username);
    when(context.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(context);

    when(userRepository.findByUsername(username))
      .thenReturn(Optional.of(userEntity));
    when(personRepository.findByUserUserId(userEntity.getUserId()))
      .thenReturn(Collections.emptyList());

    List<LoanEntity> result = loanService.getLoansByUser();

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(userRepository).findByUsername(username);
    verify(personRepository).findByUserUserId(userEntity.getUserId());
  }


/**
 * Testet das Abrufen der aktiven Leihvorgänge für den eingeloggten Benutzer.
 * Zu erwarten: Eine Liste mit nur den aktiven Leihvorgängen zurückgegeben wird (Leihvorgänge, die nicht zurückgegeben wurden).
 */
  @Test
  void testGetActiveLoansByUser_shouldReturnActiveLoans() {
    String username = "testuser";
    UserEntity userEntity = new UserEntity();
    userEntity.setUserId(2L);

    LoanEntity activeLoan = new LoanEntity();
    activeLoan.setReturnedAt(null); // Active

    when(userRepository.findByUsername(username))
      .thenReturn(Optional.of(userEntity));
    when(loanRepository.findByPerson_User_UserIdAndReturnedAtIsNull(2L))
      .thenReturn(Collections.singletonList(activeLoan));

    List<LoanEntity> result = loanService.getActiveLoansByUser();

    assertEquals(1, result.size());
    assertEquals(activeLoan, result.get(0));
  }

  /**
 * Testet das Abrufen der aktiven Leihvorgänge für den eingeloggten Benutzer, wenn der Benutzer nicht gefunden wird.
 * Zu erwarten:`UsernameNotFoundException` geworfen, wenn der Benutzer mit dem angegebenen Benutzernamen nicht existiert.
 */
  @Test
  void testGetActiveLoansByUser_whenUserNotFound_shouldThrowUsernameNotFoundException() {
    String username = "testuser";

    SecurityContext context = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    when(authentication.getName()).thenReturn(username);
    when(context.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(context);

    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    UsernameNotFoundException exception = assertThrows(
      UsernameNotFoundException.class,
      () -> loanService.getActiveLoansByUser()
    );
    assertEquals(
      "User not found with username: " + username,
      exception.getMessage()
    );
    verify(userRepository).findByUsername(username);
  }

  /**
 * Testet das Abrufen der überfälligen Leihvorgänge für den eingeloggten Benutzer.
 * Zu erwarten: Liste der überfälligen Leihvorgänge zurückgegeben, wenn die Leihvorgänge das Rückgabedatum überschritten haben 
 * und nicht zurückgegeben wurden.
 */
  @Test
  void testGetOverdueLoansByUser_shouldReturnOverdueLoans() {
    String username = "testuser";
    UserEntity userEntity = new UserEntity();
    userEntity.setUserId(3L);

    LoanEntity overdueLoan = new LoanEntity();
    overdueLoan.setDueDate(LocalDate.now().minusDays(1));
    overdueLoan.setReturnedAt(null);

    when(userRepository.findByUsername(username))
      .thenReturn(Optional.of(userEntity));
    when(
      loanRepository.findByPerson_User_UserIdAndDueDateBeforeAndReturnedAtIsNull(
        eq(3L),
        any(LocalDate.class)
      )
    )
      .thenReturn(Collections.singletonList(overdueLoan));

    List<LoanEntity> overdueLoans = loanService.getOverdueLoansByUser(
      LocalDate.now()
    );

    assertEquals(1, overdueLoans.size());
    assertEquals(overdueLoan, overdueLoans.get(0));
  }

  /**
 * Testet das Abrufen der überfälligen Leihvorgänge für den eingeloggten Benutzer, wenn der Benutzer nicht gefunden wird.
 * Zu erwarten: `UsernameNotFoundException` geworfen, wenn der Benutzer mit dem angegebenen Benutzernamen nicht existiert.
 */
  @Test
  void testGetOverdueLoansByUser_whenUserNotFound_shouldThrowUsernameNotFoundException() {
    String username = "testuser";
    LocalDate currentDate = LocalDate.now();

    SecurityContext context = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    when(authentication.getName()).thenReturn(username);
    when(context.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(context);

    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    UsernameNotFoundException exception = assertThrows(
      UsernameNotFoundException.class,
      () -> loanService.getOverdueLoansByUser(currentDate)
    );

    assertEquals(
      "User not found with username: " + username,
      exception.getMessage()
    );

    verify(userRepository).findByUsername(username);
    verifyNoInteractions(loanRepository);
  }
}
