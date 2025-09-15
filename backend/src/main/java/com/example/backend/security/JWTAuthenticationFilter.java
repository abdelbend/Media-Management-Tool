package com.example.backend.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter zur Bearbeitung von JWT-Authentifizierung.
 * Überprüft die Anforderung auf JWT und authentifiziert
 * den Benutzer, wenn das Token gültig ist.
 */
public class JWTAuthenticationFilter extends OncePerRequestFilter {

  private final JWTGenerator tokenGenerator;

  private final CustomUserDetailsService customUserDetailsService;

    /**
   * Konstruktor für die Initialisierung des Filters.
   * 
   * @param tokenGenerator JWT-Generator, der zur Validierung
   * und Extraktion von Informationen ausTokens verwendet wird.
   * @param customUserDetailsService benutzerdefinierte Service, der
   * Benutzerinformationen anbahnd des Benutzernamen lädt.
   */
  public JWTAuthenticationFilter(
    JWTGenerator tokenGenerator,
    CustomUserDetailsService customUserDetailsService
  ) {
    this.tokenGenerator = tokenGenerator;
    this.customUserDetailsService = customUserDetailsService;
  }

  /**
   * Überprüft JWT-Token und authentifiziert den Benutzer, falls der
   * Token gültig ist.
   * 
   * @param request Eingehende HTTP-Anforderung.
   * @param response HTTP-Antwort.
   * @param filterChain Filterkette, die nach dem Filter fortgesetzt wird.
   * @throws ServletException Wenn eine Fehlerbedingung während der Filterung auftritt.
   * @throws IOException Wenn ein Fehler beim Lesen der Anforderung oder
   * Schreiben der Antwort auftritt.
   */
  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {
    String token = getJWTFromRequest(request);

    if (StringUtils.hasText(token) && tokenGenerator.validateToken(token)) {
      String username = tokenGenerator.getUserNameFromJWT(token);

      UserDetails userDetails = customUserDetailsService.loadUserByUsername(
        username
      );
      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        userDetails.getAuthorities()
      );
      authenticationToken.setDetails(
        new WebAuthenticationDetailsSource().buildDetails(request)
      );
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
    filterChain.doFilter(request, response);
  }

  /**
   * Extrahiert JWT aus dem "Authorization"-Header der HTTP-Anforderung.
   * 
   * @param request Eingehende HTTP-Anforderung.
   * @return JWT, das aus dem Header extrahiert wurde, oder {@code null},
   * wenn kein Token gefunden wurde.
   */
  private String getJWTFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7, bearerToken.length());
      // ab 7 da "Bearer " sieben Zeichen hat und wir nur  den Token haben wollen.
    }
    return null;
  }
}
