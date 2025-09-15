package com.example.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

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

@ExtendWith(MockitoExtension.class)
public class LoanServiceWriteTests {

  @Mock
  private LoanRepository loanRepository;

  @Mock
  private UserService userService;

  @Mock
  private MediaService mediaService;

  @Mock
  private MediaRepository mediaRepository;

  @Mock
  private PersonService personService;

  @InjectMocks
  private LoanService loanService;

  @BeforeEach
  void setupSecurityContext() {
    // Mock security context in order to set the current user
    SecurityContext context = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    lenient().when(authentication.getName()).thenReturn("testuser");
    lenient().when(context.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(context);
  }

  /**
   * Testet die erfolgreiche Erstellung eines Leihvorgangs.
   * Überprüft, ob der Leihvorgang korrekt erstellt wird, wenn alle Eingabewerte gültig sind.
   */
  @Test
  void testCreateLoan_shouldCreateLoanSuccessfully() {
    String username = "testuser";
    UserEntity userEntity = new UserEntity();
    userEntity.setUserId(1L);

    MediaEntity mediaEntity = new MediaEntity();
    mediaEntity.setMediaId(10L);
    mediaEntity.setMediaState(MediaState.AVAILABLE);

    PersonEntity personEntity = new PersonEntity();
    personEntity.setPersonId(100L);

    when(userService.getUserByUsername(username))
      .thenReturn(Optional.of(userEntity));
    when(mediaService.getMediaById(10L)).thenReturn(Optional.of(mediaEntity));
    when(personService.getPersonById(100L))
      .thenReturn(Optional.of(personEntity));
    when(loanRepository.save(any(LoanEntity.class)))
      .thenAnswer(invocation -> invocation.getArgument(0));
    when(mediaRepository.save(any(MediaEntity.class)))
      .thenAnswer(invocation -> invocation.getArgument(0));

    LocalDate dueDate = LocalDate.now().plusDays(14);
    LocalDateTime borrowedAt = LocalDateTime.now().minusDays(1);
    LoanEntity createdLoan = loanService.createLoan(
      10L,
      100L,
      SecurityContextHolder.getContext().getAuthentication(),
      dueDate,
      borrowedAt
    );

    // Assert
    assertNotNull(createdLoan);
    assertEquals(personEntity, createdLoan.getPerson());
    assertEquals(mediaEntity, createdLoan.getMedia());
    assertEquals(dueDate, createdLoan.getDueDate());
    assertEquals(borrowedAt, createdLoan.getBorrowedAt());
    assertEquals(MediaState.BORROWED, mediaEntity.getMediaState());

    verify(userService).getUserByUsername(username);
    verify(mediaService).getMediaById(10L);
    verify(personService).getPersonById(100L);
    verify(loanRepository).save(any(LoanEntity.class));
    verify(mediaRepository).save(any(MediaEntity.class));
  }

   /**
   * Testet, dass beim Erstellen eines Leihvorgangs, wenn keine Werte für 'borrowedAt' und 'dueDate'
   * angegeben sind, Standardwerte gesetzt werden.
   */
  @Test
  void testCreateLoan_whenBorrowedAtAndDueDateAreNull_shouldSetDefaultValues() {
    // Arrange
    String username = "testuser";
    UserEntity userEntity = new UserEntity();
    userEntity.setUserId(1L);

    MediaEntity mediaEntity = new MediaEntity();
    mediaEntity.setMediaId(10L);
    mediaEntity.setMediaState(MediaState.AVAILABLE);

    PersonEntity personEntity = new PersonEntity();
    personEntity.setPersonId(100L);

    when(userService.getUserByUsername(username))
      .thenReturn(Optional.of(userEntity));
    when(mediaService.getMediaById(10L)).thenReturn(Optional.of(mediaEntity));
    when(personService.getPersonById(100L))
      .thenReturn(Optional.of(personEntity));
    when(loanRepository.save(any(LoanEntity.class)))
      .thenAnswer(invocation -> invocation.getArgument(0));
    when(mediaRepository.save(any(MediaEntity.class)))
      .thenAnswer(invocation -> invocation.getArgument(0));

    LoanEntity createdLoan = loanService.createLoan(
      10L,
      100L,
      SecurityContextHolder.getContext().getAuthentication(),
      null,
      null
    );

    // Assert
    assertNotNull(createdLoan);
    assertNotNull(createdLoan.getBorrowedAt());
    assertNotNull(createdLoan.getDueDate());

    assertTrue(
      createdLoan.getBorrowedAt().isAfter(LocalDateTime.now().minusSeconds(5))
    );
    assertTrue(
      createdLoan.getBorrowedAt().isBefore(LocalDateTime.now().plusSeconds(5))
    );
    assertEquals(LocalDate.now().plusMonths(1), createdLoan.getDueDate());

    assertEquals(MediaState.BORROWED, mediaEntity.getMediaState());

    verify(userService).getUserByUsername(username);
    verify(mediaService).getMediaById(10L);
    verify(personService).getPersonById(100L);
    verify(loanRepository).save(any(LoanEntity.class));
    verify(mediaRepository).save(any(MediaEntity.class));
  }


  /**
   * Testet, dass eine 'UserNotFoundException' geworfen wird, wenn der Benutzer nicht gefunden wird.
   */
  @Test
  void testCreateLoan_whenUserNotFound_shouldThrowUserNotFoundException() {
    when(userService.getUserByUsername("testuser"))
      .thenReturn(Optional.empty());

    assertThrows(
      UserNotFoundException.class,
      () ->
        loanService.createLoan(
          10L,
          100L,
          SecurityContextHolder.getContext().getAuthentication(),
          null,
          null
        )
    );
  }


  /**
   * Testet, dass eine 'MediaNotFoundException' geworfen wird, wenn das Medium nicht gefunden wird.
   */
  @Test
  void testCreateLoan_whenMediaNotFound_shouldThrowMediaNotFoundException() {
    // Arrange
    String username = "testuser";
    UserEntity userEntity = new UserEntity();
    userEntity.setUserId(1L);

    when(userService.getUserByUsername(username))
      .thenReturn(Optional.of(userEntity));
    when(mediaService.getMediaById(999L)).thenReturn(Optional.empty());

    assertThrows(
      MediaNotFoundException.class,
      () ->
        loanService.createLoan(
          999L,
          100L,
          SecurityContextHolder.getContext().getAuthentication(),
          null,
          null
        )
    );
  }

  /**
   * Testet, dass eine Ausnahme geworfen wird, wenn das Medium bereits ausgeliehen oder nicht verfügbar ist.
   */
  @Test
  void testCreateLoan_whenMediaIsBorrowedOrUnavailable_shouldThrowRuntimeException() {
    String username = "testuser";
    UserEntity userEntity = new UserEntity();
    userEntity.setUserId(1L);

    when(userService.getUserByUsername(username))
      .thenReturn(Optional.of(userEntity));

    MediaEntity mediaEntity = new MediaEntity();

    for (MediaState state : List.of(
      MediaState.BORROWED,
      MediaState.UNAVAILABLE
    )) {
      mediaEntity.setMediaId(10L);
      mediaEntity.setMediaState(state);

      when(mediaService.getMediaById(10L)).thenReturn(Optional.of(mediaEntity));

      RuntimeException exception = assertThrows(
        RuntimeException.class,
        () ->
          loanService.createLoan(
            10L,
            100L,
            SecurityContextHolder.getContext().getAuthentication(),
            null,
            null
          )
      );

      assertEquals("Media is not available for loan", exception.getMessage());
    }

    verify(userService, times(2)).getUserByUsername(username);
    verify(mediaService, times(2)).getMediaById(10L);
    verify(loanRepository, never()).save(any());
    verify(mediaRepository, never()).save(any());
    verifyNoInteractions(personService);
  }

   /**
   * Testet, dass eine 'PersonNotFoundException' geworfen wird, wenn die Person nicht gefunden wird.
   */
  @Test
  void testCreateLoan_whenPersonNotFound_shouldThrowPersonNotFoundException() {
    String username = "testuser";
    UserEntity userEntity = new UserEntity();
    userEntity.setUserId(1L);

    MediaEntity mediaEntity = new MediaEntity();
    mediaEntity.setMediaId(10L);
    mediaEntity.setMediaState(MediaState.AVAILABLE);

    when(userService.getUserByUsername(username))
      .thenReturn(Optional.of(userEntity));
    when(mediaService.getMediaById(10L)).thenReturn(Optional.of(mediaEntity));
    when(personService.getPersonById(999L)).thenReturn(Optional.empty());

    assertThrows(
      PersonNotFoundException.class,
      () ->
        loanService.createLoan(
          10L,
          999L,
          SecurityContextHolder.getContext().getAuthentication(),
          null,
          null
        )
    );
  }


  /**
   * Testet das Markieren eines Leihvorgangs als zurückgegeben und die Aktualisierung des Medienstatus.
   */
  @Test
  void testMarkAsReturned_shouldUpdateLoanAndMediaState() {
    LoanEntity loanEntity = new LoanEntity();
    loanEntity.setLoanId(123L);
    loanEntity.setBorrowedAt(LocalDateTime.now().minusDays(5));
    loanEntity.setReturnedAt(null);

    MediaEntity mediaEntity = new MediaEntity();
    mediaEntity.setMediaId(10L);
    mediaEntity.setMediaState(MediaState.BORROWED);
    loanEntity.setMedia(mediaEntity);

    when(loanRepository.findById(123L)).thenReturn(Optional.of(loanEntity));
    when(loanRepository.save(any(LoanEntity.class)))
      .thenAnswer(invocation -> invocation.getArgument(0));
    when(mediaRepository.save(any(MediaEntity.class)))
      .thenAnswer(invocation -> invocation.getArgument(0));

    LocalDateTime now = LocalDateTime.now();

    loanService.markAsReturned(123L, now);

    assertEquals(now, loanEntity.getReturnedAt());
    assertEquals(MediaState.AVAILABLE, mediaEntity.getMediaState());
    verify(loanRepository).findById(123L);
    verify(loanRepository).save(loanEntity);
    verify(mediaRepository).save(mediaEntity);
  }

    /**
   * Testet, dass eine Ausnahme geworfen wird, wenn der Leihvorgang bereits zurückgegeben wurde.
   * Überprüft, dass eine RuntimeException mit der erwarteten Fehlermeldung geworfen wird.
   */
  @Test
  void testMarkAsReturned_whenLoanAlreadyReturned_shouldThrowException() {
    // Arrange
    LoanEntity loanEntity = new LoanEntity();
    loanEntity.setLoanId(123L);
    loanEntity.setBorrowedAt(LocalDateTime.now().minusDays(5));
    loanEntity.setReturnedAt(LocalDateTime.now());

    when(loanRepository.findById(123L)).thenReturn(Optional.of(loanEntity));

    RuntimeException exception = assertThrows(
      RuntimeException.class,
      () -> loanService.markAsReturned(123L, LocalDateTime.now())
    );
    assertEquals("Loan already returned with ID: 123", exception.getMessage());
    verify(loanRepository).findById(123L);
    verify(loanRepository, never()).save(any());
  }


  /**
   * Testet, dass eine Ausnahme geworfen wird, wenn das Rückgabedatum vor dem Ausleihdatum liegt.
   * Überprüft, dass eine IllegalArgumentException mit der erwarteten Fehlermeldung geworfen wird.
   */
  @Test
  void testMarkAsReturned_whenReturnedDateBeforeBorrowedDate_shouldThrowException() {
    // Arrange
    LoanEntity loanEntity = new LoanEntity();
    loanEntity.setLoanId(123L);
    loanEntity.setBorrowedAt(LocalDateTime.now());
    loanEntity.setReturnedAt(null);

    when(loanRepository.findById(123L)).thenReturn(Optional.of(loanEntity));

    LocalDateTime invalidReturnDate = LocalDateTime.now().minusDays(1);

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> loanService.markAsReturned(123L, invalidReturnDate)
    );
    assertEquals(
      "Returned date cannot be before borrowed date for loan with ID: 123",
      exception.getMessage()
    );
    verify(loanRepository).findById(123L);
    verify(loanRepository, never()).save(any());
  }

    /**
   * Testet, dass eine Ausnahme geworfen wird, wenn die Leih-ID null ist.
   * Überprüft, dass eine IllegalArgumentException mit der entsprechenden Fehlermeldung geworfen wird.
   */
  @Test
  void testMarkAsReturned_whenLoanIdNull_shouldThrowException() {
    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> loanService.markAsReturned(null, LocalDateTime.now())
    );
    assertEquals("Loan ID cannot be null", exception.getMessage());
    verifyNoInteractions(loanRepository, mediaRepository);
  }

    /**
   * Testet, dass eine Ausnahme geworfen wird, wenn das Ausleihdatum für einen Leihvorgang nicht gesetzt ist.
   * Überprüft, dass eine RuntimeException mit der entsprechenden Fehlermeldung geworfen wird.
   */
  @Test
  void testMarkAsReturned_whenBorrowedAtIsNull_shouldThrowException() {
    // Arrange
    LoanEntity loanEntity = new LoanEntity();
    loanEntity.setLoanId(123L);
    loanEntity.setBorrowedAt(null);
    loanEntity.setReturnedAt(null);

    when(loanRepository.findById(123L)).thenReturn(Optional.of(loanEntity));

    RuntimeException exception = assertThrows(
      RuntimeException.class,
      () -> loanService.markAsReturned(123L, LocalDateTime.now())
    );
    assertEquals(
      "Borrowed date is not set for loan with ID: 123",
      exception.getMessage()
    );
  }

  /**
   * Testet, dass das Rückgabedatum auf den aktuellen Zeitpunkt gesetzt wird, wenn es null ist.
   * Überprüft, dass das Rückgabedatum korrekt gesetzt und der Medienstatus auf "VERFÜGBAR" aktualisiert wird.
   */
  @Test
  void testMarkAsReturned_whenReturnedAtIsNull_shouldSetToNow() {
    LoanEntity loanEntity = new LoanEntity();
    loanEntity.setLoanId(123L);
    loanEntity.setBorrowedAt(LocalDateTime.now().minusDays(5));
    loanEntity.setReturnedAt(null);

    MediaEntity mediaEntity = new MediaEntity();
    mediaEntity.setMediaId(10L);
    mediaEntity.setMediaState(MediaState.BORROWED);
    loanEntity.setMedia(mediaEntity);

    when(loanRepository.findById(123L)).thenReturn(Optional.of(loanEntity));
    when(loanRepository.save(any(LoanEntity.class)))
      .thenAnswer(invocation -> invocation.getArgument(0));
    when(mediaRepository.save(any(MediaEntity.class)))
      .thenAnswer(invocation -> invocation.getArgument(0));

    loanService.markAsReturned(123L, null);

    assertNotNull(loanEntity.getReturnedAt());
    assertTrue(
      loanEntity.getReturnedAt().isAfter(LocalDateTime.now().minusSeconds(5))
    );
    assertTrue(
      loanEntity.getReturnedAt().isBefore(LocalDateTime.now().plusSeconds(5))
    );
    assertEquals(MediaState.AVAILABLE, mediaEntity.getMediaState());

    verify(loanRepository).findById(123L);
    verify(loanRepository).save(loanEntity);
    verify(mediaRepository).save(mediaEntity);
  }
}
