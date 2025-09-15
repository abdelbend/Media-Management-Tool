package com.example.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.AuthResponseDTO;
import com.example.backend.dto.LoginDTO;
import com.example.backend.dto.RegisterDTO;
import com.example.backend.exception.LoginException;
import com.example.backend.model.UserEntity;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JWTGenerator;

/**
 * Controller-Klasse: Zuständig für die Handhabung von Authentifizierungs- 
 * und Benutzerregistrierungsanfragen.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  private final JWTGenerator jwtGenerator;

  /**
   * Konstruktor: {@code AuthController}.
   *
   * @param authenticationManager Manager für die Authentifizierung von Benutzerdaten
   * @param userRepository Repository für die Verwaltung von Benutzerdaten
   * @param passwordEncoder Encoder für das sichere Speichern von Passwörtern
   * @param jwtGenerator Generator für die Erstellung von JWT-Authentifizierungs-Tokens
   */
  public AuthController(
    AuthenticationManager authenticationManager,
    UserRepository userRepository,
    PasswordEncoder passwordEncoder,
    JWTGenerator jwtGenerator
  ) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtGenerator = jwtGenerator;
  }

  /**
   * Methode bezüglich Benutzerregistrierungsanfragen.
   *
   * @param registerDto Registrierungsdaten des Benutzers
   * @return Eine {@link ResponseEntity}, die eine Erfolgs- oder Fehlermeldung enthält
   */
  @PostMapping("register")
  public ResponseEntity<String> register(@RequestBody RegisterDTO registerDto) {
    // Überprüfen, ob der Benutzername bereits existiert
    if (userRepository.existsByUsername(registerDto.getUsername())) {
      return new ResponseEntity<>("Username is taken!", HttpStatus.BAD_REQUEST);
    }

    // Überprüfen, ob die E-Mail-Adresse bereits existiert
    if (userRepository.existsByEmail(registerDto.getEmail())) {
      return ResponseEntity.badRequest().body("Email is taken!");
    }

    // Überprüfen, ob alle erforderlichen Daten angegeben wurden
    if (
      registerDto.getUsername() == null ||
      registerDto.getEmail() == null ||
      registerDto.getPassword() == null
    ) {
      return new ResponseEntity<>(
        "Please provide username, email and password",
        HttpStatus.BAD_REQUEST
      );
    }

    // Benutzer erstellen und speichern
    UserEntity user = new UserEntity();
    user.setUsername(registerDto.getUsername());
    user.setEmail(registerDto.getEmail());
    user.setPassword(passwordEncoder.encode((registerDto.getPassword())));

    userRepository.save(user);

    return new ResponseEntity<>(
      "User registered successfully",
      HttpStatus.CREATED
    );
  }

  /**
   * Benutzer-Login-Anfragen.
   *
   * @param loginDto  Login-Daten des Benutzers
   * @return Eine {@link ResponseEntity}, die das Authentifizierungs-Token enthält, falls der Login erfolgreich ist
   * @throws LoginException Wenn der Benutzername oder das Passwort ungültig ist
   */
  @PostMapping("login")
  public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO loginDto) {
    try {
      // Benutzer authentifizieren
      Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
          loginDto.getUsername(),
          loginDto.getPassword()
        )
      );

      // Sicherheitskontext aktualisieren
      SecurityContextHolder.getContext().setAuthentication(authentication);

      // JWT-Token generieren
      String token = jwtGenerator.generateToken(authentication);

      return new ResponseEntity<>(
        new AuthResponseDTO(token, token),
        HttpStatus.OK
      );
    } catch (Exception ex) {
      throw new LoginException("Invalid username or password");
    }
  }
}
