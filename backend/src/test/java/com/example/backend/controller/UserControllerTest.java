package com.example.backend.controller;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.backend.model.UserEntity;
import com.example.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Autowired
  private ObjectMapper objectMapper;

  private UserEntity testUser;

  @BeforeEach
  void setUp() {
    testUser = new UserEntity();
    testUser.setUserId(1L);
    testUser.setUsername("testuser");
    testUser.setEmail("testuser@example.com");
    testUser.setPassword("password123");
  }

  /**
 * Testet das Abrufen der Benutzer, wenn fünf oder weniger Benutzer vorhanden sind.
 * Zu erwarten: Liste der Benutzer mit einem Benutzer zurückgegeben.
 */
  @Test
  void testGetUsersIfFiveOrLess() throws Exception {
    when(userService.getUsersIfFiveOrLess())
      .thenReturn(Arrays.asList(testUser));

      // Sendet eine GET-Anfrage und prüft, ob der Benutzer korrekt zurückgegeben wird.
    mockMvc
      .perform(get("/api/users/returnUsers"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].userId", is(1)))
      .andExpect(jsonPath("$[0].username", is("testuser")));

       // Überprüft, ob die entsprechende Service-Methode aufgerufen wurde.
    verify(userService, times(1)).getUsersIfFiveOrLess();
  }

  @Test
  void testGetUsersIfFiveOrLess_NoUsers() throws Exception {
    when(userService.getUsersIfFiveOrLess()).thenReturn(Arrays.asList());

    mockMvc
      .perform(get("/api/users/returnUsers"))
      .andExpect(status().isNoContent());

    verify(userService, times(1)).getUsersIfFiveOrLess();
  }

  /**
 * Testet das Abrufen der Benutzer, wenn keine Benutzer vorhanden sind.
 * Zu erwarten: 204 No Content Status, wenn keine Benutzer gefunden werden.
 */
  @Test
  @WithMockUser(username = "testuser")
  void testGetUserByUsername() throws Exception {
    // Mock service
    when(userService.getUserByUsername("testuser"))
      .thenReturn(Optional.of(testUser));

       // Sendet eine GET-Anfrage und erwartet einen 204 No Content Status
    mockMvc
      .perform(get("/api/users/testuser"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.userId", is(1)))
      .andExpect(jsonPath("$.username", is("testuser")));

      // Überprüft, ob die entsprechende Service-Methode aufgerufen wurde
    verify(userService, times(1)).getUserByUsername("testuser");
  }

  /**
 * Testet das Abrufen eines Benutzers anhand des Benutzernamens.
 * Zu erwarten: Benutzer mit dem angegebenen Benutzernamen korrekt zurückgegeben.
 */
  @Test
  @WithMockUser(username = "testuser")
  void testGetUserByUsername_NotFound() throws Exception {
    when(userService.getUserByUsername("unknownuser"))
      .thenReturn(Optional.empty());

      // Sendet eine GET-Anfrage und prüft, ob der Benutzer mit dem richtigen Namen zurückgegeben wird
    mockMvc
      .perform(get("/api/users/unknownuser"))
      .andExpect(status().isNotFound());

    // Überprüft, ob die entsprechende Service-Methode aufgerufen wurde
    verify(userService, times(1)).getUserByUsername("unknownuser");
  }
  

/**
 * Testet den Fall, dass ein Benutzer mit dem angegebenen Benutzernamen nicht gefunden wird.
 * Zu erwarten: 404 Not Found Status.
 */
  @Test
  @WithMockUser(username = "testuser")
  void testUpdateUser() throws Exception {
    when(userService.updateUser(eq(1L), any(UserEntity.class)))
      .thenReturn(testUser);

    // Sendet eine GET-Anfrage und erwartet einen 404 Not Found Status
    mockMvc
      .perform(
        put("/api/users/1")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(testUser))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.userId", is(1)))
      .andExpect(jsonPath("$.username", is("testuser")));

    // Überprüft, ob die entsprechende Service-Methode aufgerufen wurde
    verify(userService, times(1)).updateUser(eq(1L), any(UserEntity.class));
  }

  /**
 * Testet das Aktualisieren eines Benutzers anhand seiner ID.
 * Zu erwaten:Benutzerdaten erfolgreich aktualisiert werden und korrekten Werte werden zurückgegeben.
 */
  @Test
  @WithMockUser(username = "testuser")
  void testDeleteUser() throws Exception {

    // Sendet eine PUT-Anfrage zur Aktualisierung des Benutzers und prüft die Rückgabewerte
    mockMvc.perform(delete("/api/users/1")).andExpect(status().isNoContent());

    // Überprüft, ob die entsprechende Service-Methode aufgerufen wurde
    verify(userService, times(1)).deleteUser(1L);
  }
}
