package com.example.backend.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import com.example.backend.model.PersonEntity;
import com.example.backend.model.UserEntity;
import com.example.backend.repository.PersonRepository;

public class PersonServiceTest {

  @Mock
  private PersonRepository personRepository;

  @Mock
  private UserService userService;

  @Mock
  private Authentication authentication;

  @InjectMocks
  private PersonService personService;

  private PersonEntity person;
  private UserEntity user;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);

    user = new UserEntity();
    user.setUserId(1L);
    user.setUsername("testUser");

    person =
      new PersonEntity(
        1L,
        user,
        "testFirstName",
        "testLastName",
        "testAddress",
        "testEmail@test.com",
        "123456",
        LocalDateTime.now(),
        null
      );
  }
  /**
   * Testet das Abrufen einer Person anhand ihrer ID.
   * Überprüft, dass die Person korrekt zurückgegeben wird, wenn sie in der Datenbank vorhanden ist.
   */
  @Test
  public void testGetPersonById() {
    when(personRepository.findById(1L))
      .thenReturn(java.util.Optional.of(person));

    Optional<PersonEntity> result = personService.getPersonById(1L);

    assertTrue(result.isPresent());
    assertEquals("testFirstName", result.get().getFirstName());
    assertEquals("testLastName", result.get().getLastName());
    assertEquals("testAddress", result.get().getAddress());
    assertEquals("testEmail@test.com", result.get().getEmail());
    assertEquals("123456", result.get().getPhone());

    verify(personRepository, times(1)).findById(1L);
  }

  /**
   * Testet das Abrufen einer Person anhand ihrer ID, wenn die Person nicht gefunden wird.
   * Überprüft, dass eine leere Option zurückgegeben wird, wenn keine Person mit der angegebenen ID existiert.
   */
  @Test
  public void testGetPersonByIdNotFound() {
    when(personRepository.findById(1L)).thenReturn(java.util.Optional.empty());

    Optional<PersonEntity> result = personService.getPersonById(1L);

    assertTrue(result.isEmpty());
    verify(personRepository, times(1)).findById(1L);
  }

   /**
   * Testet das Abrufen von Personen anhand der Benutzer-ID.
   * Überprüft, dass alle Personen korrekt zurückgegeben werden, wenn der Benutzer entsprechende Einträge hat.
   */
  @Test
  public void testGetPersonByUserId() {
    when(personRepository.findByUserUserId(1L))
      .thenReturn(Arrays.asList(person));

    List<PersonEntity> persons = personService.getPersonsByUserId(1L);

    assertEquals(1, persons.size());
    assertEquals(person, persons.get(0));
    assertEquals("testFirstName", persons.get(0).getFirstName());

    verify(personRepository, times(1)).findByUserUserId(1L);
  }


  /**
   * Testet das Abrufen von Personen anhand der Benutzer-ID, wenn keine Personen gefunden werden.
   * Überprüft, dass eine leere Liste zurückgegeben wird, wenn keine Personen zur Benutzer-ID existieren.
   */
  @Test
  public void testGetPersonByUserIdNotFound() {
    when(personRepository.findByUserUserId(1L)).thenReturn(Arrays.asList());

    List<PersonEntity> persons = personService.getPersonsByUserId(1L);

    assertEquals(0, persons.size());
    verify(personRepository, times(1)).findByUserUserId(1L);
  }


  /**
   * Testet das erfolgreiche Erstellen einer neuen Person.
   * Überprüft, dass die Person korrekt erstellt wird und die Daten richtig gespeichert werden.
   */
  @Test
  public void testdeletePersonSuccess() {
    when(authentication.getName()).thenReturn("testUser");
    when(userService.getUserByUsername("testUser"))
      .thenReturn(Optional.of(user));
    when(personRepository.save(any(PersonEntity.class))).thenReturn(person);

    PersonEntity createdPerson = personService.createPerson(
      person,
      authentication
    );

    assertNotNull(createdPerson);
    assertEquals("testFirstName", createdPerson.getFirstName());
    assertEquals("testLastName", createdPerson.getLastName());
    assertEquals("testAddress", createdPerson.getAddress());
    assertEquals("testEmail@test.com", createdPerson.getEmail());
    assertEquals("123456", createdPerson.getPhone());

    verify(authentication, times(1)).getName();
    verify(userService, times(1)).getUserByUsername("testUser");
    verify(personRepository, times(1)).save(any(PersonEntity.class));
  }


  /**
   * Testet das Erstellen einer Person, wenn der Benutzer nicht gefunden wird.
   * Überprüft, dass eine RuntimeException geworfen wird, wenn der Benutzer für die angegebene ID nicht existiert.
   */
  @Test
  public void testdeletePerson_UserNotFound() {
    when(authentication.getName()).thenReturn("testUser");
    when(userService.getUserByUsername("testUser"))
      .thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(
      RuntimeException.class,
      () -> {
        personService.createPerson(person, authentication);
      }
    );

    assertEquals("User not found", exception.getMessage());
    verify(authentication, times(1)).getName();
    verify(userService, times(1)).getUserByUsername("testUser");
    verify(personRepository, never()).save(any(PersonEntity.class));
  }

    /**
   * Testet das erfolgreiche Aktualisieren einer Person.
   * Überprüft, dass die Person mit den neuen Details korrekt aktualisiert wird.
   */
  @Test
  public void testUpdatePerson() {
    when(authentication.getName()).thenReturn(user.getUsername());

    when(userService.getUserByUsername(user.getUsername()))
      .thenReturn(Optional.of(user));

    when(personRepository.findById(person.getPersonId()))
      .thenReturn(Optional.of(person));

    when(personRepository.save(any(PersonEntity.class)))
      .thenAnswer(invocation -> invocation.getArgument(0));

    PersonEntity personDetails = new PersonEntity();
    personDetails.setFirstName("UpdatedFirstName");
    personDetails.setLastName("UpdatedLastName");
    personDetails.setAddress("UpdatedAddress");
    personDetails.setEmail("updated.email@example.com");
    personDetails.setPhone("987654321");

    PersonEntity updatedPerson = personService.updatePerson(
      person.getPersonId(),
      personDetails,
      authentication
    );

    assertNotNull(updatedPerson);
    assertEquals("UpdatedFirstName", updatedPerson.getFirstName());
    assertEquals("UpdatedLastName", updatedPerson.getLastName());
    assertEquals("UpdatedAddress", updatedPerson.getAddress());
    assertEquals("updated.email@example.com", updatedPerson.getEmail());
    assertEquals("987654321", updatedPerson.getPhone());
    assertEquals(user, updatedPerson.getUser());

    verify(authentication, times(1)).getName();
    verify(userService, times(1)).getUserByUsername(user.getUsername());
    verify(personRepository, times(1)).findById(person.getPersonId());
    verify(personRepository, times(1)).save(any(PersonEntity.class));
  }

   /**
   * Testet das Aktualisieren einer Person, wenn die Person nicht gefunden wird.
   * Überprüft, dass eine RuntimeException geworfen wird, wenn keine Person mit der angegebenen ID existiert.
   */
  @Test
  public void testUpdatePerson_NotFound() {
    when(authentication.getName()).thenReturn(user.getUsername());

    when(userService.getUserByUsername(user.getUsername()))
      .thenReturn(Optional.of(user));

    when(personRepository.findById(person.getPersonId()))
      .thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(
      RuntimeException.class,
      () -> {
        personService.updatePerson(
          person.getPersonId(),
          person,
          authentication
        );
      }
    );

    assertEquals("Person not found", exception.getMessage());

    verify(authentication, times(1)).getName();
    verify(userService, times(1)).getUserByUsername(user.getUsername());
    verify(personRepository, times(1)).findById(person.getPersonId());
    verify(personRepository, never()).save(any(PersonEntity.class));
  }
}
