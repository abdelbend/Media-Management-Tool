package com.example.backend.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Benutzerdefinierte Implementierung von {@link AuthenticationEntryPoint}
 * Zuständig für Behandlung von nicht-autorisierten Anfragen.
 */
@Component
public class JWTAuthEntryPoint implements AuthenticationEntryPoint {

    /**
     * Methode augerufen, wenn Zugriff auf eine geschützte Ressource ohne
     * gültige Authentifizierung erfolgt. Sendet eine HTTP-Antwort mit
     * Statuscode {@code 401 Unauthorized} und einer Fehlermeldung.
     *
     * @param request Eingehende HTTP-Anforderung.
     * @param response HTTP-Antwort, die an den Client gesendet wird.
     * @param authException Ausnahme, die während der Authentifizierung
     * aufgetreten ist.
     * @throws IOException Wenn ein Fehler beim Schreiben der Antwort auftritt.
     * @throws ServletException Wenn ein Fehler bei der Verarbeitung der Anfrage
     * auftritt.
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        response.sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized access: " + authException.getMessage()
        );
    }
}
