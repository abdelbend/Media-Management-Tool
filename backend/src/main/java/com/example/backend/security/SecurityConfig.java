package com.example.backend.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Enthält die Sicherheitskonfiguration für die Webanwendung.
 * Konfiguriert Authentifizierung, die Autorisierung und CORS-Einstellungen.
 * Implementiert die Sicherheitsrichtlinien mit der JWT-Authentifizierung,
 * des Passwort-Codierens, des Schutzes gegen CSRF-Angriffe und der CORS-Konfiguration.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JWTAuthEntryPoint authEntryPoint;

  private final CustomUserDetailsService userDetailsService;

  private final JWTGenerator jwtGenerator;


  /**
   * Konstruktor, der die erforderlichen Abhängigkeiten injiziert.
   * @param userDetailsService Service, der für das Laden von Benutzerdaten zuständig ist.
   * @param authEntryPoint Einstiegspunkt für Authentifizierungsfehler.
   * @param jwtGenerator Generator für JWT-Token.
   */
  public SecurityConfig(
    CustomUserDetailsService userDetailsService,
    JWTAuthEntryPoint authEntryPoint,
    JWTGenerator jwtGenerator
  ) {
    this.userDetailsService = userDetailsService;
    this.authEntryPoint = authEntryPoint;
    this.jwtGenerator = jwtGenerator;
  }

    /**
   * Konfiguriert Sicherheitsfilterkette, einschließlich CORS, CSRF, Authentifizierung
   * und Autorisierung von HTTP-Anfragen.
   * @param http HTTP-Sicherheitskonfiguration.
   * @return konfigurierte Sicherheitsfilterkette.
   * @throws Exception Wenn eine Fehler beim Konfigurieren der Filter auftreten.
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable()) // Deaktiviert CSRF-Schutz für stateless APIs
      .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Aktiviert CORS mit benutzerdefinierten Einstellungen
      .exceptionHandling(exceptionHandling ->
        exceptionHandling.authenticationEntryPoint(authEntryPoint) // Legt den Authentifizierungs-Einstiegspunkt fest
      )
      .sessionManagement(sessionManagement ->
        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      .authorizeHttpRequests(authorize ->
        authorize 
          .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
          .permitAll()
          .requestMatchers(
            "/api/auth/**",
            "/api/auth/login",
            "/api/auth/register"
          )
          .permitAll() // Öffentliche Zugriffe auf Swagger UI und OpenAPI-Dokumentation
          .requestMatchers(HttpMethod.GET, "/api/users/returnUsers")
          .permitAll() // Öffentliche Endpunkte (Login, Registrierung, etc.)
          .requestMatchers("/api/**")
          .authenticated() // Authentifizierung für andere /api/** Endpunkte erforderlich
          .anyRequest()
          .authenticated() // Alle anderen Anfragen erfordern Authentifizierung
      ) 
      .addFilterBefore(
        jwtAuthenticationFilter(), // Fügt JWT-Authentifizierungsfilter vor dem Standard-UsernamePasswordAuthenticationFilter hinzu
        UsernamePasswordAuthenticationFilter.class
      );

    return http.build();
  }

  /**
   * Konfiguriert AuthenticationManager, der für die Authentifizierung von Benutzern zuständig ist.
   * @param authenticationConfiguration Konfiguration für die Authentifizierung.
   * @return AuthenticationManager.
   * @throws Exception Wenn ein Fehler beim Abrufen des AuthenticationManagers auftritt.
   */
  @Bean
  public AuthenticationManager authenticationManager(
    AuthenticationConfiguration authenticationConfiguration
  ) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  /**
   * Konfiguriert PasswordEncoder, der für die Passwortsicherung verwendet wird.
   * @return Der PasswordEncoder.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // 12 is a higher strength, adjust as needed
  }


  /**
   * Erstellt JWT-Authentifizierungsfilter, der das Token überprüft und Benutzer authentifiziert.
   * @return JWT-Authentifizierungsfilter.
   */
  @Bean
  public JWTAuthenticationFilter jwtAuthenticationFilter() {
    return new JWTAuthenticationFilter(jwtGenerator, userDetailsService);
  }

  /**
   * Konfiguriert CORS-Einstellungen für die Anwendung.
   * @return Konfigurierte CORS-Einstellungen.
   */
  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Erlaubt Anfragen von den angegebenen Frontend-URLs
    configuration.setAllowedOrigins(
      Arrays.asList("http://localhost:5173", "http://localhost:3000")
    );

    // Erlaubt alle HTTP-Methoden
    configuration.setAllowedMethods(
      Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")
    );

    // Erlaubt alle Header während der Entwicklung
    configuration.setAllowedHeaders(Arrays.asList("*"));

    // Erlaubt die Verwendung von Cookies oder JWTs in Anfragen
    configuration.setAllowCredentials(true);

    // Setzt die globale CORS-Konfiguration
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
