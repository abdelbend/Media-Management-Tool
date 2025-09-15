package com.example.backend.service;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.backend.exception.MediaNotFoundException;
import com.example.backend.exception.PersonNotFoundException;
import com.example.backend.model.LoanEntity;
import com.example.backend.model.MediaEntity;
import com.example.backend.model.MediaState;
import com.example.backend.model.UserEntity;
import com.example.backend.repository.LoanRepository;

@ExtendWith(MockitoExtension.class)
class LoanServiceEdgeCaseTest {

  @Mock
  private LoanRepository loanRepository;

  @Mock
  private UserService userService;

  @Mock
  private MediaService mediaService;

  @Mock
  private PersonService personService;

  @Mock
  private Authentication authentication;

  @InjectMocks
  private LoanService loanService;

  @BeforeEach
  void setupSecurityContext() {
    SecurityContext context = mock(SecurityContext.class);
    SecurityContextHolder.setContext(context);
  }

  /**
 * Testet das Erstellen eines Leihvorgangs, wenn das Medium bereits ausgeliehen wurde.
 * Zu erwarten: RuntimeException mit der Nachricht "Media is not available for loan" geworfen,
 * wenn das Medium bereits den Status "BORROWED" hat.
 */
  @Test
  void testCreateLoan_whenMediaAlreadyBorrowed_shouldThrowRuntimeException() {
    String username = "edgeUser";

    UserEntity user = new UserEntity();
    user.setUserId(42L);

    MediaEntity mediaEntity = new MediaEntity();
    mediaEntity.setMediaId(555L);
    mediaEntity.setMediaState(MediaState.BORROWED);

    Authentication authenticationMock = mock(Authentication.class);
    when(authenticationMock.getName()).thenReturn(username);

    SecurityContext securityContextMock = mock(SecurityContext.class);
    when(securityContextMock.getAuthentication())
      .thenReturn(authenticationMock);
    SecurityContextHolder.setContext(securityContextMock);

    when(userService.getUserByUsername(username)).thenReturn(Optional.of(user));
    when(mediaService.getMediaById(555L)).thenReturn(Optional.of(mediaEntity));

    RuntimeException exception = assertThrows(
      RuntimeException.class,
      () ->
        loanService.createLoan(
          555L,
          1000L,
          SecurityContextHolder.getContext().getAuthentication(),
          null,
          null
        )
    );

    assertEquals("Media is not available for loan", exception.getMessage());

    verify(userService).getUserByUsername(username);
    verify(mediaService).getMediaById(555L);
    verifyNoInteractions(personService);
  }

  /**
 * Testet das Erstellen eines Leihvorgangs, wenn das Medium nicht gefunden wird.
 * HZu erwarten: `MediaNotFoundException` geworfen, wenn das Medium mit der angegebenen ID nicht existiert.
 */
  @Test
  void testCreateLoan_MediaNotFound() {
    Long mediaId = 1L;
    Long personId = 1L;
    String username = "testuser";

    UserEntity mockUser = new UserEntity();
    mockUser.setUsername(username);

    when(authentication.getName()).thenReturn(username);
    when(userService.getUserByUsername(username))
      .thenReturn(Optional.of(mockUser));
    when(mediaService.getMediaById(mediaId)).thenReturn(Optional.empty()); // Nur Stub für mediaService

    MediaNotFoundException exception = assertThrows(
      MediaNotFoundException.class,
      () ->
        loanService.createLoan(mediaId, personId, authentication, null, null)
    );
    assertEquals("Media not found with ID: " + mediaId, exception.getMessage());

    verify(authentication).getName();
    verify(userService).getUserByUsername(username);
    verify(mediaService).getMediaById(mediaId);
    verifyNoMoreInteractions(
      userService,
      mediaService,
      personService,
      loanRepository
    );
  }

  /**
 * Testet das Markieren eines Leihvorgangs als zurückgegeben, wenn das Medium bereits als zurückgegeben markiert wurde.
 * Zu erwarten: RuntimeException geworfen, wenn der Leihvorgang bereits ein Rückgabedatum hat.
 */
  @Test
  void testMarkAsReturned_whenLoanAlreadyHasReturnedAt_shouldThrowException() {
    LoanEntity loan = new LoanEntity();
    loan.setLoanId(999L);
    loan.setBorrowedAt(LocalDateTime.now().minusDays(2));
    loan.setReturnedAt(LocalDateTime.now().minusHours(1)); // Bereits zurück

    when(loanRepository.findById(999L)).thenReturn(Optional.of(loan));

    assertThrows(
      RuntimeException.class,
      () -> loanService.markAsReturned(999L, LocalDateTime.now())
    );
    verify(loanRepository, never()).save(any());
  }

  /**
 * Testet das Markieren eines Leihvorgangs als zurückgegeben, wenn der Leihvorgang nicht gefunden wird.
 * Zu erwarten:RuntimeException geworfen, wenn der Leihvorgang mit der angegebenen ID nicht existiert.
 */
  @Test
  void testMarkAsReturned_whenLoanNotFound_shouldThrowRuntimeException() {
    when(loanRepository.findById(123L)).thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(
      RuntimeException.class,
      () -> loanService.markAsReturned(123L, LocalDateTime.now())
    );
    assertTrue(ex.getMessage().contains("Loan not found with ID: 123"));
  }


/**
 * Testet das Markieren eines Leihvorgangs als zurückgegeben, wenn das Rückgabedatum vor dem Entleihdatum liegt.
 * Zu erwarten:`IllegalArgumentException` geworfen, wenn das Rückgabedatum vor dem Entleihdatum liegt.
 */
  @Test
  void testMarkAsReturned_whenReturnedAtBeforeBorrowedAt_shouldThrowIllegalArgumentException() {
    // Arrange
    LoanEntity loan = new LoanEntity();
    loan.setLoanId(200L);
    loan.setBorrowedAt(LocalDateTime.now().plusDays(1)); // Ungültig: Zukünftiges BorrowedAt

    when(loanRepository.findById(200L)).thenReturn(Optional.of(loan));

    assertThrows(
      IllegalArgumentException.class,
      () -> loanService.markAsReturned(200L, LocalDateTime.now())
    );
    verify(loanRepository, never()).save(any()); // Sicherstellen, dass nichts gespeichert wird
  }


/**
 * Testet das Erstellen eines Leihvorgangs, wenn die Person nicht gefunden wird.
 * Zu erwarten: `PersonNotFoundException` geworfen wird, wenn die Person mit der angegebenen ID nicht existiert.
 */
  @Test
  void testCreateLoan_whenPersonNotFound_shouldThrowPersonNotFoundException() {
    String username = "edgeUser";

    UserEntity user = new UserEntity();
    user.setUserId(42L);

    MediaEntity mediaEntity = new MediaEntity();
    mediaEntity.setMediaId(555L);
    mediaEntity.setMediaState(MediaState.AVAILABLE);

    Authentication authenticationMock = mock(Authentication.class);
    when(authenticationMock.getName()).thenReturn(username);

    SecurityContext securityContextMock = mock(SecurityContext.class);
    when(securityContextMock.getAuthentication())
      .thenReturn(authenticationMock);
    SecurityContextHolder.setContext(securityContextMock);

    when(userService.getUserByUsername(username)).thenReturn(Optional.of(user));
    when(mediaService.getMediaById(555L)).thenReturn(Optional.of(mediaEntity));
    when(personService.getPersonById(999L)).thenReturn(Optional.empty());

    PersonNotFoundException exception = assertThrows(
      PersonNotFoundException.class,
      () ->
        loanService.createLoan(
          555L,
          999L,
          SecurityContextHolder.getContext().getAuthentication(),
          null,
          null
        )
    );

    assertEquals("Person not found with ID: 999", exception.getMessage());

    verify(userService).getUserByUsername(username);
    verify(mediaService).getMediaById(555L);
    verify(personService).getPersonById(999L);
    verifyNoMoreInteractions(userService, mediaService, personService);
  }
}
